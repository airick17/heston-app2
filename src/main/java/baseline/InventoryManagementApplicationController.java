/*
 *  UCF COP3330 Fall 2021 Application Assignment 2 Solution
 *  Copyright 2021 Eric Heston
 */

package baseline;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryManagementApplicationController implements Initializable {

    //fxml controls
    @FXML
    private TableView<Item> inventoryTableView;
    @FXML
    private TableColumn<Item, String> serialNumberColumn;
    @FXML
    private TableColumn<Item, String> nameColumn;
    @FXML
    private TableColumn<Item, String> priceColumn;
    @FXML
    private Button addItemButton;
    @FXML
    private TextField serialNumberTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private Label addItemWarning;
    @FXML
    private Button modifyButton;
    @FXML
    private Button searchButton;
    @FXML
    private ChoiceBox<String> searchByChoiceBox;
    @FXML
    private Button loadSelectedItemButton;
    @FXML
    private Button removeItemButton;


    //sets up table view collections
    ObservableList<Item> inventoryList = FXCollections.observableArrayList();

    //change ?
    //Item selectedItem = null;
    //SortedList<Item> sortedItems;

    //sets up FXMl file
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //sets up table columns
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumberString"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        //sets up combo box
        searchByChoiceBox.getItems().addAll("Item Name", "Serial Number");
    }

    //creates an item from the text fields and adds it to saved list then updates table view
    @FXML
    private void addItemClick(ActionEvent e) {
        if(createItem() != null){
            inventoryList.addAll(createItem());
            inventoryTableView.setItems(inventoryList);
            refreshItemAddTextFields();
            addItemWarning.setText(null);
        }
    }

    //removes item from inventory List
    @FXML
    private void removeItemClick(ActionEvent e){
        inventoryList.removeAll(inventoryTableView.getSelectionModel().getSelectedItem());
        inventoryTableView.refresh();
    }

    //creates an item instance
    private Item createItem() {
        if(serialNumberValid() && nameValid() && priceValid()){
            return new Item(serialNumberTextField.getText().trim(),nameTextField.getText().trim(),priceTextField.getText().trim());
        }
        return null;
    }

    //checks price is valid before making new item
    private boolean priceValid() {
        //gets input as a string
        String price = priceTextField.getText().trim();
        //try to parse to double if it can't it means it contains non-numerical chars
        try{
            double priceNum = Double.parseDouble(price);
            //if it can be converted to double it makes sure it is greater than 0
            return priceNum > 0;
        } catch (NumberFormatException e){
            addItemWarning.setText("The item's price must be a number greater than 0.");
            return false;
        }
    }

    //checks name is 2-256 chars long
    private boolean nameValid() {
        String name = nameTextField.getText().trim();
        checkNameError();
        return name.length() >= 2 && name.length() <= 256;
    }

    //sets error label if name is invalid
    private void checkNameError() {
        String name = nameTextField.getText().trim();
        if(name.length() < 2 || name.length() > 256){
            addItemWarning.setText("Name must be between 2 and 256 characters long.");
        }
    }

    //checks serial number is in correct format using regex
    private boolean serialNumberValid() {
        final Pattern pattern = Pattern.compile("[A-Z]-[\\w][\\w][\\w]-[\\w][\\w][\\w]-[\\w][\\w][\\w]", Pattern.CASE_INSENSITIVE);
        String serialNum = serialNumberTextField.getText().trim();
        final Matcher matcher = pattern.matcher(serialNum);
        if(checkIfSerialNumberExists()){
            return false;
        }else if(matcher.matches()){
            return true;
        }else {
            addItemWarning.setText("The serial number must be in the format (A-XXX-XXX-XXX).");
            return false;
        }
    }

    //will check serialNum text field to see if serial num exists before creating the new item
    private boolean checkIfSerialNumberExists() {
        //gets entered data
        String serialNum = serialNumberTextField.getText().trim();
        //makes a new array list equal to current inventoryList
        ArrayList<Item> temp = new ArrayList<>(inventoryList);
        //loops through inventory to check if the serial num already exists
        for (Item item : temp) {
            if (item.getSerialNumberString().contains(serialNum)) {
                addItemWarning.setText("The serial number entered already exists."); //warning label to user
                return true;
            }
        }
        return false;
    }

    //clears text fields for item add
    private void refreshItemAddTextFields() {
        serialNumberTextField.clear();
        nameTextField.clear();
        priceTextField.clear();
    }

}
