package ro.mpp2024;

public class Donatie extends Entity<Long>{

    private CazCaritabil cazCaritabil;

    private Donator donator;

    private Integer suma;

    @Override
    public String toString() {
        return "Donatie{" +
                "cazCaritabil=" + cazCaritabil +
                ", donator=" + donator +
                ", suma=" + suma +
                '}';
    }

    public void setCazCaritabil(CazCaritabil cazCaritabil) {
        this.cazCaritabil = cazCaritabil;
    }

    public void setDonator(Donator donator) {
        this.donator = donator;
    }

    public void setSuma(Integer suma) {
        this.suma = suma;
    }

    public CazCaritabil getCazCaritabil() {
        return cazCaritabil;
    }

    public Donator getDonator() {
        return donator;
    }

    public Integer getSuma() {
        return suma;
    }

    public Donatie(CazCaritabil cazCaritabil, Donator donator, Integer suma) {
        this.cazCaritabil = cazCaritabil;
        this.donator = donator;
        this.suma = suma;
    }
}
