package ro.mpp2024;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "Voluntar" )
public class Voluntar extends ro.mpp2024.Entity<Long>{

    private String nume;

    private String prenume;

    private String username;

    private String parola;

    public Voluntar() {

    }


    @Id
    @GeneratedValue(generator="increment")
    public Long getId() {
        return super.getId();
    }

    public void setId(Long id) {
        super.setId(id);
    }

    @Override
    public String toString() {
        return "Voluntar{" +
                "nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", username='" + username + '\'' +
                ", parola='" + parola + '\'' +
                '}';
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    @Column(name = "nume")
    public String getNume() {
        return nume;
    }

    @Column(name = "prenume")
    public String getPrenume() {
        return prenume;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    @Column(name = "parola")
    public String getParola() {
        return parola;
    }

    public Voluntar(String nume, String prenume, String username, String parola) {
        this.nume = nume;
        this.prenume = prenume;
        this.username = username;
        this.parola = parola;
    }
}
