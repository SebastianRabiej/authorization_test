import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.json.JSON
import spock.lang.Shared
import spock.lang.Specification


class AuthorizationSpec extends Specification {
    @Shared RESTClient restClient

    def setupSpec() {
        restClient = new RESTClient("http://46.101.141.19:9000", JSON)
        restClient.handler.failure = { resp -> resp } //FIX niedoskonałości RestClienta.
    }

    def cleanup() {
        restClient.post(path: "/clearUsers")
    }

    def "powinien poprawnie zalogować się do systemu"(){
        given: "Zakładając, że w systemie istnieje użytkownik o loginie Admin i hasle Admin"
        restClient.post(path: "/addUser", body: new LoginDTO("Admin", "Admin"), contentType: ContentType.JSON)

        when: "Gdy chce zalogować się na konto Admina, podając hasło Admin"
        HttpResponseDecorator resp  = restClient.post(path: "/login", body: new LoginDTO("Admin", "Admin"), contentType: ContentType.JSON)

        then: "Wtedy powinienem dostać informację, o numerze statusu 200."
        resp.status == 200
    }

    def "powinien dostac błąd logowania o numerze 401"(){
        given: "Zakładając, że w systemie istnieje użytkownik o loginie Admin i hasle Admin"
        restClient.post(path: "/addUser", body: new LoginDTO("Admin", "Admin"), contentType: ContentType.JSON)

        when: "Gdy chce zalogować się na konto Admina, podając hasło Ela123"
        HttpResponseDecorator resp  = restClient.post(path: "/login", body: new LoginDTO("Admin", "Ela123"), contentType: ContentType.JSON)

        then: "Wtedy powinienem dostać informację, że jest błąd logowania o numerze statusu 401."
        resp.status == 401
    }

    def "powinien dostac błąd logowania o numerze 404"(){
        given: "Zakładając, że w systemie nie istnieje użytkownik o loginie FAIL_USER"
        //nic nie robi

        when: "Gdy chce zalogować się na konto Admina, podając hasło Ela123"
        HttpResponseDecorator resp  = restClient.post(path: "/login", body: new LoginDTO("FAIL_USER", "WHAT_EVA"), contentType: ContentType.JSON)

        then: "Wtedy powinienem dostać informację, że użytkownik nie istnieję numerze statusu 404."
        resp.status == 404
    }
}