package ro.mpp2024;


public interface IVoluntarRepository extends Repository<Long, Voluntar> {

    public Voluntar findVoluntarByUsernameandPassword(String username, String password);

}
