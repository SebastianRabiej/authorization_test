import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.json.JSON
import org.tools4j.groovytables.GroovyTables
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll


@Narrative(""" Zakładając, że stan bazy danych wygląda następująco :
            login   | password
            Admin   | Admin
            Sebek   | Urtica
            
            Użytkownicy starają się zalogować do aplikacji.
            """)
@Title("Logowanie do serwera z jednorazowym mockiem użytkowników")
class AuthorizationWithManyTablesSpec extends Specification {

    @Shared RESTClient restClient

    def setupSpec() {
        restClient = new RESTClient("http://46.101.141.19:9000", JSON)
        restClient.handler.failure = { resp -> resp } //FIX niedoskonałości RestClienta.
        fillWithUsersData{
            login   | password
            "Admin" | "Admin"
            "Sebek" | "Urtica"
        }
    }

    def cleanupSpec() {
        restClient.post(path: "/clearUsers")
    }

    def fillWithUsersData(Closure closure) {
        List<LoginDTO> loginDtos = GroovyTables.createListOf(LoginDTO.class).fromTable(closure)
        for (LoginDTO loginDTO : loginDtos) {
            restClient.post(path: "/addUser", body: new LoginDTO(loginDTO.getLogin(), loginDTO.getPassword()), contentType: ContentType.JSON)
        }
    }

    @Unroll
    def "powinien zwrocic #statusCode dla loginu: #login i hasla: #haslo"(){
        given: ""
        //@implSpec - zostało to zrobione w setupSpec ponieważ chcemy przyśpieszyć nasze testy

        when: "Gdy chce zalogować się na konto #login, podając hasło #haslo"
        HttpResponseDecorator resp  = restClient.post(path: "/login", body: new LoginDTO(login, haslo), contentType: ContentType.JSON)

        then: "Wtedy powinienem dostać informację o numerze #statusCode."
        resp.status == statusCode

        where:
        login       | haslo          | statusCode
        "Admin"     | "Admin"        | 200
        "Admin"     | "Ela123"       | 401
        "Sebek"     | "Urtica"       | 200
        "Sebek"     | "Bledne"       | 401
        "FAIL_USER" | "Bledne"       | 404
    }
}