package controller;

import TM.dtmTM;
import bo.BOFactory;
import bo.custom.CustomerBO;
import bo.custom.Impl.CustomerBOImpl;
import bo.custom.Impl.ItemBOImpl;
import bo.custom.Impl.PlaceOrderBOImpl;
import bo.custom.ItemBO;
import bo.custom.PlaceOrderBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import dto.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.textfield.TextFields;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import static javafx.scene.input.KeyCode.I;

public class PlaceOrderController implements Initializable {
    public Label txtDate;
    public Label txtTime;
    public JFXComboBox CustomerComboTxt;
    public JFXTextField txtCustName;
    public JFXTextField txtCustPhone;
    public JFXTextField txtCustAddress;
    public JFXTextField txtCustEmail;
    public Label txtTotal;
    public TableView<dtmTM> dtm;
    public TableColumn colitemcode;
    public TableColumn colitemname;
    public TableColumn colprice;
    public TableColumn colqty;
    public JFXTextField txtItemCode;
    public JFXTextField txtItemName;
    public JFXTextField txtPrice;
    public JFXTextField txtQTY;
    public Label txtOrderId;
    public Label txtQtyCount;
    public Label finalTotal;
    public JFXTextField Discount;
    public Label txtCashierID;
    public JFXButton btnRemove;
    CustomerBO customerBO = new CustomerBOImpl();
    CustomerDTO customerDTO;
    ItemDTO itemDTO;
    ItemBO itemBO = new ItemBOImpl();
    ArrayList<String> allCustomers = new ArrayList<>();
    ObservableList<CustomerDTO> customerDTOS;
    ArrayList<String> allItem = new ArrayList<>();
    ObservableList<OrdersDTO> ordersDTOS;
    PlaceOrderBO placeOrderBO = new PlaceOrderBOImpl();
    ArrayList<dtmTM> items = new ArrayList<>();
    int count;
    int total;
    dtmTM dtmTM;
    private int index;
    ArrayList<ItemDTO>itemDTOS;

    public PlaceOrderController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colitemcode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colitemname.setCellValueFactory(new PropertyValueFactory<>("name"));
        colprice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colqty.setCellValueFactory(new PropertyValueFactory<>("QTY"));
        generateDateTime();
        getAllCustomer();
        reloadCustomer();
        getAllItem();
        setTxtorderID();
        TextFields.bindAutoCompletion(txtItemCode, allItem);
    }

    public void setTxtorderID() {
        try {
            int id = placeOrderBO.getRowCount();
//                this.txtOrderId.setText("K001");
            if (id < 9) {
                this.txtOrderId.setText("K00" + (id + 1));
            } else if (id < 99) {
                this.txtOrderId.setText("K0" + (id + 1));
            } else {
                this.txtOrderId.setText("K" + (id + 1));
            }
        } catch (ClassNotFoundException | SQLException var2) {
            var2.printStackTrace();
        }
    }

    private void getAllCustomer() {
        try {
            customerDTOS = customerBO.getAllCustomer();
            for (CustomerDTO dto : customerDTOS) {
                allCustomers.add(dto.getCustID() + " " + dto.getCustName());
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private void getAllItem() {
        try {
            ObservableList<ItemDTO> itemDTOS = itemBO.getAllItem();
            for (ItemDTO dto : itemDTOS) {
                allItem.add(dto.getItemCode() + " " + dto.getDescription());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void generateDateTime() {
        this.txtDate.setText(LocalDate.now().toString());
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, (e) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
            this.txtTime.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1.0D)));
        timeline.setCycleCount(-1);
        timeline.play();


    }


    public void CustomerCombo(ActionEvent actionEvent) {


    }

    private void reloadCustomer() {
        CustomerComboTxt.setItems(FXCollections.observableArrayList(allCustomers));
        TextFields.bindAutoCompletion(CustomerComboTxt.getEditor(), allCustomers);
    }


    public void searchItemOnAction() {
        try {
            String itemID = txtItemCode.getText();
            String[] a = itemID.split("\\s+");
            ItemDTO searchItem = itemBO.searchItem(a[0]);
            txtItemName.setText(searchItem.getDescription());
            txtPrice.setText(searchItem.getUnitPrice());

            if (searchItem != null) {


            } else {
                String tilte = "Searched Item Not Found";
                String message = "Try Again";
                tray.notification.TrayNotification tray = new TrayNotification();
                AnimationType type = AnimationType.POPUP;

                tray.setAnimationType(type);
                tray.setTitle(tilte);
                tray.setMessage(message);
                tray.setNotificationType(NotificationType.ERROR);
                tray.showAndDismiss(Duration.millis(3000));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void btnComboOnAction(ActionEvent actionEvent) {
        try {
            String value = CustomerComboTxt.getValue().toString();
            String[] a = value.split("\\s+");
            CustomerDTO customerDTO = customerBO.searchCustomer(a[0]);
            txtCustName.setText(customerDTO.getCustName());
            txtCustAddress.setText(customerDTO.getCustAddress());
            txtCustEmail.setText(customerDTO.getCustEmail());
            txtCustPhone.setText(customerDTO.getCustPhoneNo());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public void addCartOnAction() throws SQLException, ClassNotFoundException {
        String code = txtItemCode.getText();
        String name = txtItemName.getText();
        String price = txtPrice.getText();
        String QTY = txtQTY.getText();
        dtmTM rowData = new dtmTM(code, name, price, QTY);
        items.add(rowData);
        dtm.setItems(FXCollections.observableArrayList(items));
        count += Integer.parseInt(txtQTY.getText());
        txtQtyCount.setText(count + "");
        double total = 0;
        for (int i = 0; i < count; i++) {
            total += Double.parseDouble(txtPrice.getText());
        }
        txtTotal.setText(String.valueOf(total));
        finalTotaladd();
    }

    private void finalTotaladd() {
        finalTotal.setText(txtTotal.getText());
    }

    public void RemoveOnAction(ActionEvent actionEvent) {
        items.remove(index);
        dtm.setItems(FXCollections.observableArrayList(items));
        reloadTotalQty();
    }

    private void reloadTotalQty() {
        int qty = 0;
        int total = 0;
        for (dtmTM dtmTM : items) {
            qty += Integer.parseInt(dtmTM.getQTY());
            double p = Double.parseDouble(dtmTM.getPrice());
            total += p * Integer.parseInt(dtmTM.getQTY());
        }
        txtQtyCount.setText(qty + "");
        txtTotal.setText(total + "");
    }


    public void tblMouseClick(MouseEvent mouseEvent) {
        dtmTM c = dtm.getSelectionModel().getSelectedItem();
        txtItemCode.setText(c.getCode());
        txtItemName.setText(c.getName());
        txtPrice.setText(c.getPrice());
    }

//    public double discountKey(double price, int qty, int discount) {
//        if (discount == 0) {
//            return price * qty;
//        } else {
//            return (price * qty - ((price * discount) / 100));
//        }
//    }


    public boolean placeOrderOnAction(ActionEvent actionEvent) {

        placeOrderBO = (PlaceOrderBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PO);

        String orderID = txtOrderId.getText();
        String orderDate = txtDate.getText();
        String orderTime = txtTime.getText();
        String custName = txtCustName.getText();
        String custPhoneNo = txtCustPhone.getText();
        String custAddress = txtCustAddress.getText();
        String custEmail = txtCustEmail.getText();
        String castID = txtCashierID.getText();

        double amount = Double.parseDouble(finalTotal.getText());
        String discount = Discount.getText();

//        items
        try {
            boolean placeOrdered = placeOrderBO.placeOrder(new OrdersDTO(orderID, orderDate, orderTime, custName, custPhoneNo, custAddress, custEmail, castID, items, amount, discount));

            if (placeOrdered) {

                OrderDetailFieldRest();
                (new Alert(Alert.AlertType.CONFIRMATION, "Order Successfully", new ButtonType[]{ButtonType.OK})).show();
                String tilte = "ORDER SUCCESS";
                String message = "SAY THANK YOU FOR CUSTOMER";
                tray.notification.TrayNotification tray = new TrayNotification();
                AnimationType type = AnimationType.POPUP;

                tray.setAnimationType(type);
                tray.setTitle(tilte);
                tray.setMessage(message);
                tray.setNotificationType(NotificationType.SUCCESS);
                tray.showAndDismiss(Duration.millis(3000));
                System.out.println("order placed");
                setTxtorderID();

            } else {
                (new Alert(Alert.AlertType.ERROR, "Order  Unsuccessfully", new ButtonType[]{ButtonType.OK})).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}


        return false;
    }


    public void tbltemsOnMouoseReleased(MouseEvent mouseEvent) {
        index = dtm.getSelectionModel().getSelectedIndex();
        btnRemove.setDisable(false);
    }

    public void OrderDetailFieldRest() {
        txtCustName.setText(null);
        txtCustPhone.setText(null);
        txtCustAddress.setText(null);
        txtCustEmail.setText(null);
        txtTotal.setText(null);
        txtQtyCount.setText(null);
        finalTotal.setText(null);
        txtItemCode.setText(null);
        txtItemName.setText(null);
        txtPrice.setText(null);
        txtQTY.setText(null);
    }

    public void adddiscountKey(KeyEvent keyEvent) {

    }

    public void printBillOnAction(ActionEvent actionEvent) {

        try {
            InputStream is = this.getClass().getResourceAsStream("/reports/invoicee.jrxml");
            JasperReport jr = JasperCompileManager.compileReport(is);
            HashMap<String, Object> hs = new HashMap<>();
            hs.put("itemCode", colitemcode.getText());
            hs.put("itemName", colitemname.getText());
            hs.put("QTY", colqty.getText());
            hs.put("Price", colprice.getText());
            hs.put("OrderID", txtOrderId.getText());

            JasperPrint jp = JasperFillManager.fillReport(jr, hs, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jp);

        } catch (JRException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }

    public void setCashierID(String cashierID) {
        txtCashierID.setText(cashierID);
        System.out.println(cashierID+" cashierID");
    }
}










