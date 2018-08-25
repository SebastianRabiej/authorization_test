import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.json.JSON
import spock.lang.Shared
import spock.lang.Specification


class ExampleFromDevs extends Specification {

    @Shared RESTClient restClient
    @Shared String port = 0000

    def setupSpec() {
        restClient = new RESTClient("http://46.101.141.19:"+port, JSON)
        restClient.handler.failure = { resp -> resp } //FIX niedoskonałości RestClienta.
    }

    def "Przykładowy test"(){
        given:"Jakieś założenia"
        restClient.post(path: "/ApiRestowe", body: "JAKIS JSON LUB OBIEKT", contentType: ContentType.JSON)

        when:"Gdy chce odebrać dane"
        HttpResponseDecorator resp  = restClient.post(path: "/ApiRestowe", body: null, contentType: ContentType.JSON)

        then:"Aby test przechodził - pewny assert"
        1 == 1
    }
}