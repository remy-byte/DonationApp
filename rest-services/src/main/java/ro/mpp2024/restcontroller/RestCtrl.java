package ro.mpp2024.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mpp2024.CazCaritabil;
import ro.mpp2024.RepositoryDBCazCaritabil;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/cazuri-caritabile")
public class RestCtrl {

    @Autowired
    RepositoryDBCazCaritabil repositoryDBCazCaritabil;

    @CrossOrigin
    @GetMapping
    public Collection<CazCaritabil> getAll() throws SQLException {
        return (Collection<CazCaritabil>) repositoryDBCazCaritabil.findAll();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) throws SQLException {
        return new ResponseEntity<CazCaritabil>(repositoryDBCazCaritabil.findOne(id).get(), org.springframework.http.HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CazCaritabil crrRequest) throws SQLException {
        System.out.println("Creating computerRepairRequest");
        System.out.println(crrRequest);
        return new ResponseEntity<CazCaritabil>(repositoryDBCazCaritabil.save(crrRequest).get(), org.springframework.http.HttpStatus.CREATED);

    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws SQLException {
        System.out.println("Deleting computerRepairRequest");
        if (!repositoryDBCazCaritabil.findOne(id).isPresent()) {
            System.out.println("Id-ul nu exista");
            return new ResponseEntity<String>("Id-ul nu exista", org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<CazCaritabil>(repositoryDBCazCaritabil.delete(id).get(), org.springframework.http.HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CazCaritabil cazCaritabil) throws SQLException {
        System.out.println("Updating computerRepairRequest" + cazCaritabil);
        if (!id.equals(cazCaritabil.getId())) {
            System.out.println("Id-ul nu corespunde");
            return new ResponseEntity<String>("Id ul nu corespunde", org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<CazCaritabil>(repositoryDBCazCaritabil.update(cazCaritabil).get(), org.springframework.http.HttpStatus.OK);
    }



}
