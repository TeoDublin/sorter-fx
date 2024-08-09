package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ctrlStockAnomalie {
    @FXML
    private TableView<modelEtichette> tableView;
    @FXML
    private TableColumn<modelEtichette, String> A;
    @FXML
    private TableColumn<modelEtichette, String> B;
    @FXML
    private TableColumn<modelEtichette, String> C;
    @FXML
    private TableColumn<modelEtichette, String> D;
    @FXML
    private void initialize() {
        A.setCellValueFactory(cellData -> cellData.getValue().A());
        B.setCellValueFactory(cellData -> cellData.getValue().B());
        C.setCellValueFactory(cellData -> cellData.getValue().C());
        D.setCellValueFactory(cellData -> cellData.getValue().D());

        ObservableList<modelEtichette> data = FXCollections.observableArrayList(
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A"),
            new modelEtichette("AC0000721627","152827716871","COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA COMUNE_DI_AVERSA","SCATOLO A SCATOLO A")
        );
        tableView.setItems(data);
        if (!data.isEmpty()) {
            modelEtichette firstItem = data.get(0);
            A.setText(firstItem.titleA().get());
            B.setText(firstItem.titleB().get());
            C.setText(firstItem.titleC().get());
            D.setText(firstItem.titleD().get());
        }
    }

    @FXML
    private void viewTwo() {
        // Logic to switch views
    }

    @FXML
    private void print(MouseEvent event) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null) {
            double pageHeight = 800;
            double rowHeight = 40;
            double padding = 10;
            int rowsPerPage = (int) ((pageHeight - 2 * padding) / rowHeight);
            int totalRows = tableView.getItems().size();
            int totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);
            boolean success = true;
            for (int page = 0; page < totalPages; page++) {
                GridPane gridPane = new GridPane();
                gridPane.setPadding(new Insets(padding));
                gridPane.setHgap(5);
                gridPane.setVgap(5);
                BorderStroke borderStroke = new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, null, new BorderWidths(1));
                Border border = new Border(borderStroke);

                int columnIndex = 0;
                int rowIndex = 0;

                if (page == 0) {
                    gridPane.add(createPaneWithBorder(tableView.getItems().get(0).titleC().get(), border, 300), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder(tableView.getItems().get(0).titleD().get(), border, 130), columnIndex++, rowIndex);
                    rowIndex++;
                }

                int startIndex = page * rowsPerPage;
                int endIndex = Math.min(startIndex + rowsPerPage, totalRows);

                for (int i = startIndex; i < endIndex; i++) {
                    modelEtichette item = tableView.getItems().get(i);
                    columnIndex = 0;
                    gridPane.add(createPaneWithBorder(item.C().get(), border, 300), columnIndex++, rowIndex);
                    gridPane.add(createPaneWithBorder(item.D().get(), border, 130), columnIndex++, rowIndex);
                    rowIndex++;
                }

                if (page == 0) {
                    if (printerJob.showPrintDialog(null)) {
                        boolean printed = printerJob.printPage(gridPane);
                        if (printed) {
                            success = true;
                        } else {
                            success = false;
                            System.out.println("Printing failed on page " + (page + 1));
                            break;
                        }
                    } else {
                        success = false;
                        System.out.println("Print dialog was canceled.");
                        break;
                    }
                } else {
                    boolean printed = printerJob.printPage(gridPane);
                    if (!printed) {
                        success = false;
                        System.out.println("Printing failed on page " + (page + 1));
                        break;
                    }
                }
            }
            if (success) {
                printerJob.endJob();
            }
        }
    }

    private StackPane createPaneWithBorder(String textContent, Border border, double maxWidth) {
        Text text = new Text(textContent);
        StackPane pane = new StackPane(text);
        text.setWrappingWidth(maxWidth);
        pane.setBorder(border);
        pane.setPadding(new Insets(5));
        return pane;
    }

}
