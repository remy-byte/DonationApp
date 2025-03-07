package ro.mpp2024.protobuffprotocol;

import ro.mpp2024.CazCaritabil;
import ro.mpp2024.Donatie;
import ro.mpp2024.Donator;
import ro.mpp2024.Voluntar;

import java.util.ArrayList;
import java.util.List;

public class ProtoUtils {

    public static DonatieProto.Request createLoginRequest(String username, String password) {
        DonatieProto.Request.Builder request = DonatieProto.Request.newBuilder();
        DonatieProto.Voluntar user = DonatieProto.Voluntar.newBuilder().setUsername(username).setPassword(password).build();
        request.setType(DonatieProto.Request.RequestType.LOGIN);
        request.setVolunatar(user);
        return request.build();
    }

    public static DonatieProto.Request createLogoutRequest(DonatieProto.Voluntar user) {
        DonatieProto.Request.Builder request = DonatieProto.Request.newBuilder();
        request.setType(DonatieProto.Request.RequestType.LOGOUT);
        request.setVolunatar(user);
        return request.build();
    }

    public static DonatieProto.Request createGetDonatoriRequest() {
        DonatieProto.Request.Builder request = DonatieProto.Request.newBuilder();
        request.setType(DonatieProto.Request.RequestType.GET_DONATORI);
        return request.build();
    }

    public static DonatieProto.Request createGetAllCazuriCaritabileRequest() {
        DonatieProto.Request.Builder request = DonatieProto.Request.newBuilder();
        request.setType(DonatieProto.Request.RequestType.GET_ALL_CAZURI_CARITABILE);
        return request.build();
    }

    public static List<CazCaritabil> getCazuriCaritabileFromResponse(DonatieProto.Response response) {
        var cazuri = response.getCazuriCaritabileList();
        return cazuri.stream().map(c -> new CazCaritabil(c.getNume(), c.getAsociatie())).toList();
    }

    public static List<Donator> getDonatoriFromResponse(DonatieProto.Response response) {
        var donatori = response.getDonatoriList();
        return donatori.stream().map(d -> new Donator(d.getNume(), d.getPrenume(), d.getCnp(),d.getAdresa(), d.getNumar(), d.getId())).toList();
    }

    public static DonatieProto.Request createAddDonationRequest(Donatie donatie) {
        DonatieProto.CazCaritabil cazCaritabil = DonatieProto.CazCaritabil.newBuilder().setNume(donatie.getCazCaritabil().getNume_caz()).setAsociatie(donatie.getCazCaritabil().getAsociatie()).build();
        DonatieProto.Donator donator = DonatieProto.Donator.newBuilder().setNume(donatie.getDonator().getNume()).setPrenume(donatie.getDonator().getPrenume()).setCnp(donatie.getDonator().getCnp()).setAdresa(donatie.getDonator().getAdresa()).setNumar(donatie.getDonator().getNr_telefon()).build();
        DonatieProto.Request.Builder request = DonatieProto.Request.newBuilder();
        request.setType(DonatieProto.Request.RequestType.ADD_DONATION);
        request.setDonatie(DonatieProto.Donatie.newBuilder().setCazCaritabil(cazCaritabil).setDonator(donator).setSuma(donatie.getSuma()).build());
        return request.build();
    }

    public static DonatieProto.Request createGetDonationSumRequest(String name) {
        DonatieProto.Request.Builder request = DonatieProto.Request.newBuilder();
        request.setType(DonatieProto.Request.RequestType.GET_DONATION_SUM);
        request.setNumecaz(name);
        return request.build();
    }

    public static DonatieProto.Response createOkResponse(){

        DonatieProto.Response response = DonatieProto.Response.newBuilder().setType(DonatieProto.Response.ResponseType.OK).build();
        return response;
    }

    public static DonatieProto.Response createOkResponse(Voluntar voluntar){
        DonatieProto.Voluntar user = DonatieProto.Voluntar.newBuilder().setNume(voluntar.getNume()).setPrenume(voluntar.getPrenume()).setUsername(voluntar.getUsername()).setPassword(voluntar.getParola()).build();
        DonatieProto.Response response = DonatieProto.Response.newBuilder().setType(DonatieProto.Response.ResponseType.OK).setVolunatar(user).build();
        return response;
    }

    public static DonatieProto.Response createErrorResponse(String text){
        DonatieProto.Response response = DonatieProto.Response.newBuilder().setType(DonatieProto.Response.ResponseType.ERROR).setMessage(text).build();
        return response;
    }
    public static DonatieProto.Response createGetDonatoriResponse(Iterable<Donator> donatori){
        DonatieProto.Response.Builder response = DonatieProto.Response.newBuilder().setType(DonatieProto.Response.ResponseType.GET_DONATORI);
        for (var donator : donatori) {
            DonatieProto.Donator.Builder donatorBuilder = DonatieProto.Donator.newBuilder();
            donatorBuilder.setCnp(donator.getCnp());
            donatorBuilder.setNume(donator.getNume());
            donatorBuilder.setPrenume(donator.getPrenume());
            donatorBuilder.setAdresa(donator.getAdresa());
            donatorBuilder.setNumar(donator.getNr_telefon());
            response.addDonatori(donatorBuilder.build());
        }

        return response.build();
    }

    public static DonatieProto.Response createGetAllCazuriCaritabileResponse(Iterable<CazCaritabil> cazuri){
        DonatieProto.Response.Builder response = DonatieProto.Response.newBuilder().setType(DonatieProto.Response.ResponseType.GET_ALL_CAZURI_CARITABILE);
        for (var caz : cazuri) {
            DonatieProto.CazCaritabil.Builder cazBuilder = DonatieProto.CazCaritabil.newBuilder();
            cazBuilder.setNume(caz.getNume_caz());
            cazBuilder.setAsociatie(caz.getAsociatie());
            response.addCazuriCaritabile(cazBuilder.build());
        }

        return response.build();
    }

public static DonatieProto.Response createGetDonationSumResponse(Float suma){
        DonatieProto.Response.Builder response = DonatieProto.Response.newBuilder().setType(DonatieProto.Response.ResponseType.GET_DONATION_SUM);
        response.setSuma(suma);
        return response.build();
    }

    public static Voluntar getVoluntar(DonatieProto.Response response){
        var voluntar = response.getVolunatar();
        return new Voluntar(voluntar.getNume(), voluntar.getPrenume(), voluntar.getUsername(), voluntar.getPassword());

    }

    public static Voluntar getVoluntar(DonatieProto.Request request){
        var voluntar = request.getVolunatar();
        return new Voluntar("", "", voluntar.getUsername(), voluntar.getPassword());

    }


    public static Float getDonationSumFromResponse(DonatieProto.Response response) {
        return response.getSuma();
    }

    public static DonatieProto.Request createAddDonatorRequest(Donator donator) {
        DonatieProto.Donator donatorProto = DonatieProto.Donator.newBuilder().setNume(donator.getNume()).setPrenume(donator.getPrenume()).setCnp(donator.getCnp()).setAdresa(donator.getAdresa()).setNumar(donator.getNr_telefon()).build();
        DonatieProto.Request.Builder request = DonatieProto.Request.newBuilder();
        request.setType(DonatieProto.Request.RequestType.ADD_DONATOR);
        request.setDonator(donatorProto);
        return request.build();
    }
}
