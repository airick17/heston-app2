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
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryManagementApplicationController implements Initializable {
    //tableview
    @FXML
    private TableView<Item> inventoryTableView;
    @FXML
    private TableColumn<Item, String> serialNumberColumn;
    @FXML
    private TableColumn<Item, String> nameColumn;
    @FXML
    private TableColumn<Item, String> priceColumn;
    //choice box
    @FXML
    private ChoiceBox<String> searchByChoiceBox;
    //menus
    @FXML
    private MenuItem removeAllMenuItem;
    @FXML
    private MenuItem saveInventoryTSVMenuItem;
    @FXML
    private MenuItem loadInventoryTSVMenuItem;
    //text fields
    @FXML
    private TextField serialNumberTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private TextField searchTextField;
    //buttons
    @FXML
    private Button returnButton;
    @FXML
    private Button modifyButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button loadSelectedItemButton;
    @FXML
    private Button removeItemButton;
    @FXML
    private Button clearButton;
    //labels
    @FXML
    private Label numberOfItemsCounter;
    @FXML
    private Label addItemWarning;

    //sets up table view collections
    ObservableList<Item> inventoryList = FXCollections.observableArrayList();

    //sets up FXMl file
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //sets up table columns
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumberString"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        //sets up combo box
        searchByChoiceBox.getItems().addAll("Item Name", "Serial Number");
        //set counter 0 on start up
        numberOfItemsCounter.setText("0");
    }

    //creates an item from the text fields and adds it to saved list then updates table view
    @FXML
    private void addItemClick(ActionEvent e) {
        if (createItem() != null) {
            inventoryList.addAll(createItem());
            inventoryTableView.setItems(inventoryList);
            refreshItemAddTextFields();
            addItemWarning.setText(null);
        }
        counterSet();
    }

    //removes item from inventory List by removing selected item
    @FXML
    private void removeItemClick(ActionEvent e) {
        inventoryList.removeAll(inventoryTableView.getSelectionModel().getSelectedItem());
        inventoryTableView.refresh();
        counterSet();
    }

    //deletes all inventory items by clearing the list
    @FXML
    private void removeAllItems(ActionEvent e) {
        inventoryList.clear();
        inventoryTableView.refresh();
        numberOfItemsCounter.setText("0");
    }

    //loads selected items data into the text fields, so it can be edited
    //catches error if no selection is made before clicking button and gives user an error
    //clears label when item is successfully loaded
    @FXML
    private void setLoadSelectedItemButtonClick(ActionEvent e) {
        try {
            serialNumberTextField.setText(inventoryTableView.getSelectionModel().getSelectedItem().getSerialNumberString());
            nameTextField.setText(inventoryTableView.getSelectionModel().getSelectedItem().getItemName());
            priceTextField.setText(inventoryTableView.getSelectionModel().getSelectedItem().getPrice());
            addItemWarning.setText(null);
        } catch (NullPointerException f) {
            addItemWarning.setText("You did not select an item to load.");
        }
    }

    //if item is loaded then text fields changed and modify button is clicked it will replace selected item with new data
    //checks to make sure all text fields have info before modifying
    //makes sure the text fields are changed before trying to modify item
    @FXML
    private void modifyItemClick(ActionEvent e) {
        if (serialNumberTextField.getText().isEmpty() || nameTextField.getText().isEmpty() || priceTextField.getText().isEmpty()) {
            addItemWarning.setText("You did not load an item to modify.");
        } else if (Objects.equals(serialNumberTextField.getText(), inventoryTableView.getSelectionModel().getSelectedItem().getSerialNumberString())
                && Objects.equals(nameTextField.getText(), inventoryTableView.getSelectionModel().getSelectedItem().getItemName())
                && Objects.equals(priceTextField.getText(), inventoryTableView.getSelectionModel().getSelectedItem().getPrice())) {
            addItemWarning.setText("Item information must be changed in order to modify the item.");
        } else if (serialNumberValidForMod() && nameValid() && priceValid()) {
            inventoryTableView.getSelectionModel().getSelectedItem().setSerialNumberString(serialNumberTextField.getText());
            inventoryTableView.getSelectionModel().getSelectedItem().setItemName(nameTextField.getText());
            inventoryTableView.getSelectionModel().getSelectedItem().setPrice(priceTextField.getText());
            inventoryTableView.refresh();
            refreshItemAddTextFields();
            addItemWarning.setText(null);
        }
    }

    @FXML
    private void clearButtonClick(ActionEvent e) {
        searchTextField.clear();
        serialNumberTextField.clear();
        nameTextField.clear();
        priceTextField.clear();
        addItemWarning.setText(null);
    }

    //saves inventory as txt TSV file same as previous assignment save but \t instead of ,
    //catches error if user closes file chooser and displays message
    @FXML
    private void saveListTSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save");
        fc.setInitialFileName("InventoryList");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt files", "*.txt"));
        try {
            File selectedFile = fc.showSaveDialog(null);
            FileWriter writer = new FileWriter(selectedFile.getPath());
            for (Item item : inventoryList) {
                writer.write(item.getSerialNumberString()
                        + "\t" + item.getItemName()
                        + "\t" + item.getPrice()
                        + System.lineSeparator());
            }
            writer.close();
            //clears warning after a successful save
            addItemWarning.setText(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            addItemWarning.setText("You did not save the file.");
        }
    }

    //loads a TSV file
    @FXML
    private void loadListTSV() throws FileNotFoundException {
        //new file chooser allows user to select file
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("text files", "*.txt", "*.docx"));
        fc.setTitle("Load");
        File selectedFile = fc.showOpenDialog(null);
        //this try catch catches the error that occurs if the user closes file chooser without selecting and displays message at bottom of window
        try {
            //temp array list to collect data to
            ArrayList<Item> temp = new ArrayList<>();
            Scanner scanFile = new Scanner(selectedFile.getAbsoluteFile());
            //scanner scans until no more lines found each line is one item
            while (scanFile.hasNext()) {
                String lineItem = scanFile.nextLine();
                //new scanner to scan each line of the file with a tab delimiter set
                Scanner scanLine = new Scanner(lineItem);
                scanLine.useDelimiter("\t");
                Item tempItem = new Item(scanLine.next(), scanLine.next(), scanLine.next());
                temp.add(tempItem);
            }
            //clears the current inventory and sets inventory list to new temp list and updates table view
            inventoryList.clear();
            inventoryList.setAll(temp);
            inventoryTableView.setItems(inventoryList);
            //clears warning after successful load
            addItemWarning.setText(null);
        } catch (NullPointerException e) {
            addItemWarning.setText("You did not select a file to load.");
        }
        counterSet();
    }

    //resets table view to full inventory list
    @FXML
    private void returnButtonClick(ActionEvent e) {
        inventoryTableView.setItems(inventoryList);
    }

    //searches according to choice box selection
    @FXML
    private void search() {
        addItemWarning.setText(null);
        //if item name search is selected
        if (Objects.equals(searchByChoiceBox.getSelectionModel().getSelectedItem(), "Item Name")) {
            searchByName();
        //if serial number search is selected
        } else if (Objects.equals(searchByChoiceBox.getSelectionModel().getSelectedItem(), "Serial Number")) {
            searchBySerialNumber();
        }
    }

    //finds matching item by name and selects it in the table
    private void searchByName() {
        ObservableList<Item> searchedList = FXCollections.observableArrayList();
        String searchString = searchTextField.getText().trim();
        for (Item item : inventoryList) {
            if (Objects.equals(item.getItemName(), searchString)) {
                searchedList.add(item);
            }
        }
        // if search finds nothing
        if(!searchedList.isEmpty()){
            inventoryTableView.setItems(searchedList);
            addItemWarning.setText("Item(s) found.");
        }else
            addItemWarning.setText("That name does not match an item.");
    }

    //filters the table view for a serial number matching the search text field
    //if it finds it is scrolls to that item and highlights it
    private void searchBySerialNumber() {
        ObservableList<Item> searchedList = FXCollections.observableArrayList();
        String searchString = searchTextField.getText().trim();
        for (Item item : inventoryList) {
            if (Objects.equals(item.getSerialNumberString(), searchString)) {
                searchedList.add(item);
            }
        }
        // inventoryTableView.getItems().stream().filter(item -> Objects.equals(item.getItemName(), searchString)).findAny().ifPresent(searchedList::add);
        if(!searchedList.isEmpty()){
            inventoryTableView.setItems(searchedList);
            addItemWarning.setText("Item found.");
        }else
            addItemWarning.setText("That serial number does not match an item.");
    }

    //modified serialnumber validate
    private boolean serialNumberValidForMod() {
        final Pattern pattern = Pattern.compile("[A-Z]-[\\w][\\w][\\w]-[\\w][\\w][\\w]-[\\w][\\w][\\w]", Pattern.CASE_INSENSITIVE);
        String serialNum = serialNumberTextField.getText().trim();
        final Matcher matcher = pattern.matcher(serialNum);
        if(checkIfSerialNumberExistsMod()){
            return false;
        }else if(matcher.matches()){
            return true;
        }else {
            addItemWarning.setText("The serial number must be in the format (A-XXX-XXX-XXX).");
            return false;
        }
    }

    //checks for modifying only
    private boolean checkIfSerialNumberExistsMod() {
        //gets entered data
        String serialNum = serialNumberTextField.getText().trim();
        //makes a new array list equal to current inventoryList
        ArrayList<Item> temp = new ArrayList<>(inventoryList);
        //loops through inventory to check if the serial num already exists
        for (Item item : temp) {
            if (item.getSerialNumberString().contains(serialNum) && !serialNum.equals(inventoryTableView.getSelectionModel().getSelectedItem().getSerialNumberString())) {
                addItemWarning.setText("The serial number entered already exists."); //warning label to user
                return true;
            }
        }
        return false;
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

    //sets counter to size on inventory, used after load, add item, remove item
    private void counterSet(){
        int counter = inventoryList.size();
        numberOfItemsCounter.setText(String.valueOf(counter));
    }

}
