package ro.mpp2024;



public interface IDonatieRepository extends Repository<Long, Donatie>{

    public Donator findDonator(Long id);

    public CazCaritabil findCazCaritabil(Long id);

    public Float getDonationSumforCazCaritabilbyName(String name);

}
