Code Explanation / Demonstration Video: https://youtu.be/6bas49EsF_Q

User Guide:

This is an inventory management application. It allows you to create an inventory of items that have a unique serial
number, a name, and a price. Items can be added, removed, modified, and searched through. An inventory can also be saved 
or loaded from a TSV text file. 

To add an item fill out the required text fields at the bottom of the application. A serial number must be in the format
letter - number number number - number number number - number number number, and must be unique to the current inventory
of items. The name field must be filled in with a name between 2 and 256 characters long. The price must be a number 
value greater than 0. Once the fields are filled out click the add item button. The item is now created and added to 
the inventory table.

To delete an item highlight an item in the table by clicking on its row. Then click the remove item button. The selected 
item will be removed from the inventory.

To remove all items navigate to the edit menu bar item at the top of the application. Then click the menu tile remove 
all. The inventory will now be cleared of all items.

To edit an inventory items values, first click the row of the item you wish to modify. Now click the load selected item 
button under the table. The selected item's characteristics will now be loaded into the proper text fields at the 
bottom of the application. The user can now modify the values of the item, but they must still follow their proper 
formats. Once the values are as desired click the modify item button and the item will be updated with the new values.

To sort the items by their value click the top of the price column. This will toggle the items to be displayed in 
ascending / descending order by price. 
To sort the items by serial number click the top of the serial number column. This will toggle the items to be displayed
in ascending / descending order by serial number.
To sort the items by their name click the top of the name column. This will toggle the items to be displayed in
ascending / descending order by name. 

The inventory can also be searched through. To search for an existing item by name, type the name of the item you're 
looking for into the search for an item... text field. Then select the search by name choice box option. Now click the 
search button. If items matching the name are found a new table of just those items will be displayed to the user. Items
are still able to be deleted or modified from the new table.
To search for an existing item by serial number, type the serial number of the item you're
looking for into the search for an item... text field. Then select the search by serial number choice box option. Now 
click the search button. If an item matching the serial number are found a new table of just that item will be displayed 
to the user. This item is still able to be deleted or modified from the new table.

To save an inventory to storage click the file menu option in the menu bar at the top of the application. Now click the 
menu tile save inventory as TSV. A save window will open and desired name and location can be selected by the user. 
Click save and a text file is created in the selected location.

To load an inventory to the application click the file menu option in the menu bar at the top of the application. Now 
click the menu tile load inventory as TSV. A load window will open and the desired text file can be selected for 
loading. Click open and the inventory will be loaded with the data from the file.