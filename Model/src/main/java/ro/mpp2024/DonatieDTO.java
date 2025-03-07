package ro.mpp2024;

public class DonatieDTO {

    private String nume;

    private Float suma;

public DonatieDTO(String nume, Float suma) {
        this.nume = nume;
        this.suma = suma;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Float getSuma() {
        return suma;
    }

    public void setSuma(Float suma) {
        this.suma = suma;
    }
}
