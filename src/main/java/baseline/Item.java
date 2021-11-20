/*
 *  UCF COP3330 Fall 2021 Application Assignment 2 Solution
 *  Copyright 2021 Eric Heston
 */

package baseline;

public class Item {

    //item object variables
    private String serialNumberString;
    private String itemName;
    private String price;

    //constructor for an item
    public Item(String serialNumberString, String itemName, String price){
        this.serialNumberString = serialNumberString;
        this.itemName = itemName;
        this.price = price;
    }

    //getters / setters in case
    public String getSerialNumberString() {return serialNumberString;}
    public void setSerialNumberString(String serialNumberString) {this.serialNumberString = serialNumberString;}
    public String getItemName() {return itemName;}
    public void setItemName(String itemName) {this.itemName = itemName;}
    public String getPrice() {return price;}
    public void setPrice(String price) {this.price = price;}

}
