package ro.mpp2024.UI;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import ro.mpp2024.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class VoluntarController implements IObserver {


    public Label voluntarLabel;
    public TableView<CazCaritabil> tableDonatii;
    public TableColumn numeCaz;
    public TableColumn<CazCaritabil, Float> suma_totala;
    public Label search_label;
    public TextField search_field;
    public TextField nume_field;
    public TextField prenume_field;
    public TextField adress_field;
    public TextField numar_field;
    public TextField suma_field;
    public Label nume_label;
    public Label prenume_label;
    public Label adresa_label;
    public Label numar_label;
    public Label suma_label;
    public Button buttonAdd;
    public TextField cnp_field;
    public Label cnp_label;
    public ListView listDonatori;
    Properties properties = null;

    IServer iserver = null;
    ObservableList<CazCaritabil> model = FXCollections.observableArrayList();

    ObservableList<Donator> modelDonatori = FXCollections.observableArrayList();


    Voluntar voluntar = null;

    public void setProps(IServer iServer) throws SQLException, MyAppException {
       this.iserver = iServer;
    }

    public void setVoluntar(Voluntar voluntar) throws SQLException, MyAppException {
        this.voluntar = voluntar;
        voluntarLabel.setText("Welcome, " + voluntar.getNume() + " " + voluntar.getPrenume() + "!");
        initModel();
        populateCharityCasesandDonations();
        populateDonors();
    }

    public void initialize() throws SQLException {

        search_field.setVisible(false);
        search_label.setVisible(false);
        nume_field.setVisible(false);
        prenume_field.setVisible(false);
        adress_field.setVisible(false);
        numar_field.setVisible(false);
        suma_field.setVisible(false);
        nume_label.setVisible(false);
        prenume_label.setVisible(false);
        adresa_label.setVisible(false);
        numar_label.setVisible(false);
        suma_label.setVisible(false);
        buttonAdd.setVisible(false);
        listDonatori.setVisible(false);
        cnp_field.setVisible(false);
        cnp_label.setVisible(false);


        search_field.textProperty().addListener((x, y, z) -> {
            try {
                filterDonatori();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (MyAppException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void filterDonatori() throws SQLException, MyAppException {
        String nume = search_field.getText();
        if (nume.equals("")) {
            populateDonors();
        } else {
            Iterable<Donator> donatori = iserver.getDonatori();
            List<Donator> donatoriList = StreamSupport.stream(donatori.spliterator(), false)
                    .filter(donator -> donator.getNume().contains(nume))
                    .collect(Collectors.toList());
            modelDonatori.setAll(donatoriList);
            listDonatori.setItems(modelDonatori);
        }
    }

    public void initModel() throws SQLException, MyAppException {
        model.clear();
        Iterable<CazCaritabil> cazuriCaritabile = this.iserver.getAllCazuriCaritabile();

        List<CazCaritabil> cazCaritabils = StreamSupport.stream(cazuriCaritabile.spliterator(), false)
                .collect(Collectors.toList());

        model.setAll(cazCaritabils);

    }

    private void populateCharityCasesandDonations() throws SQLException, MyAppException {
        //tableDonatii.getItems().clear();

        numeCaz.setCellValueFactory(new PropertyValueFactory<CazCaritabil, String>("nume_caz"));

        suma_totala.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CazCaritabil, Float>, ObservableValue<Float>>() {
            @Override
            public ObservableValue<Float> call(TableColumn.CellDataFeatures<CazCaritabil, Float> param) {
                return new ObservableValueBase<Float>() {
                    @Override
                    public Float getValue() {
                        try {
                            return iserver.getDonationSumforCazCaritabilbyName(param.getValue().getNume_caz());
                        } catch (MyAppException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            }
        });

        tableDonatii.setItems(model);
    }


    private void populateDonors() {
        try {
            listDonatori.getItems().clear();
            modelDonatori.clear();
            Iterable<Donator> donatori = iserver.getDonatori();
            List<Donator> donatoriList = StreamSupport.stream(donatori.spliterator(), false).collect(Collectors.toList());
            modelDonatori.setAll(donatoriList);
            listDonatori.setItems(modelDonatori);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void add_donation(MouseEvent mouseEvent) {
        if (tableDonatii.getSelectionModel().getSelectedItem() != null) {
            CazCaritabil cazCaritabil = tableDonatii.getSelectionModel().getSelectedItem();
            System.out.println(cazCaritabil.getNume_caz());

            search_field.setVisible(true);
            search_label.setVisible(true);
            nume_field.setVisible(true);
            prenume_field.setVisible(true);
            adress_field.setVisible(true);
            numar_field.setVisible(true);
            suma_field.setVisible(true);
            nume_label.setVisible(true);
            prenume_label.setVisible(true);
            adresa_label.setVisible(true);
            numar_label.setVisible(true);
            suma_label.setVisible(true);
            buttonAdd.setVisible(true);
            listDonatori.setVisible(true);
            cnp_field.setVisible(true);
            cnp_label.setVisible(true);


        }
    }

    public void completeDonation(MouseEvent mouseEvent) throws SQLException, MyAppException {
        if (!nume_field.getText().isEmpty() && !prenume_field.getText().isEmpty() && !adress_field.getText().isEmpty() && !numar_field.getText().isEmpty() && !cnp_field.getText().isEmpty()){
            Donator donator = new Donator(nume_field.getText(), prenume_field.getText(),cnp_field.getText(), adress_field.getText(), numar_field.getText());
            CazCaritabil cazCaritabil = tableDonatii.getSelectionModel().getSelectedItem();
            Integer suma = Integer.parseInt(suma_field.getText());
            if (donator == null || cazCaritabil == null || suma == null) {
                return;
            }
            this.iserver.add_donator(donator);
            this.iserver.addDonation(donator,cazCaritabil,suma);
            nume_field.clear();
            prenume_field.clear();
            adress_field.clear();
            numar_field.clear();
            suma_field.clear();
            cnp_field.clear();
        }
        else {
            Donator donator = (Donator) listDonatori.getSelectionModel().getSelectedItem();
            System.out.println(donator);
            CazCaritabil cazCaritabil = tableDonatii.getSelectionModel().getSelectedItem();
            System.out.println(cazCaritabil.getNume_caz());
            Integer suma = Integer.parseInt(suma_field.getText());
            System.out.println(suma);
            if (donator == null || cazCaritabil == null || suma == null) {
                return;
            }
            System.out.println(donator.getId());
            this.iserver.addDonation(donator, cazCaritabil, suma);
        }

    }

    public void printYes() {
        System.out.println("Yes");
    }

    @Override
    public void update() throws MyAppException, SQLException {
        Platform .runLater(() -> {
            try {
                initModel();
                populateCharityCasesandDonations();
                populateDonors();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (MyAppException e) {
                e.printStackTrace();
            }
        });
    }

    public void logout(ActionEvent actionEvent) {
        try {
            this.iserver.logout(voluntar);
        } catch (MyAppException e) {
            e.printStackTrace();
        }
    }
}
