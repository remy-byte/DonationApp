package ro.mpp2024;

public class Donator extends Entity<Long> {

    private String nume;

    private String prenume;

    private String cnp;

    private String adresa;

    private String nr_telefon;

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setNr_telefon(String nr_telefon) {
        this.nr_telefon = nr_telefon;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getCnp() {
        return cnp;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getNr_telefon() {
        return nr_telefon;
    }

    public Donator(String nume, String prenume, String cnp, String adresa, String nr_telefon, Integer id) {
        this.nume = nume;
        this.prenume = prenume;
        this.cnp = cnp;
        this.adresa = adresa;
        this.nr_telefon = nr_telefon;
        this.setId(Long.valueOf(id));
    }

    public Donator(String nume, String prenume, String cnp, String adresa, String nr_telefon) {
        this.nume = nume;
        this.prenume = prenume;
        this.cnp = cnp;
        this.adresa = adresa;
        this.nr_telefon = nr_telefon;
    }

    @Override
    public String toString() {
        return  "nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'';
    }
}