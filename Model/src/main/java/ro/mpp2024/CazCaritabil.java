package ro.mpp2024;

public class CazCaritabil extends Entity<Long>{

    private String nume_caz;

    private String asociatie;

    public CazCaritabil() {
    }

    @Override
    public String toString() {
        return "{" +
                "\"nume_caz\":\""+ nume_caz + "\"," +
                "\"asociatie\":\"" + asociatie + "\"}";
    }

    public void setNume_caz(String nume_caz) {
        this.nume_caz = nume_caz;
    }

    public void setAsociatie(String asociatie) {
        this.asociatie = asociatie;
    }

    public String getNume_caz() {
        return nume_caz;
    }

    public String getAsociatie() {
        return asociatie;
    }

    public CazCaritabil(String nume_caz, String asociatie) {
        this.nume_caz = nume_caz;
        this.asociatie = asociatie;
    }
}
