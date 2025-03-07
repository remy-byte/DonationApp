package ro.mpp2024;

public interface IDonatorRepository extends Repository<Long, Donator> {

    public Donator findDonatorByCNP(String CNP);

}
