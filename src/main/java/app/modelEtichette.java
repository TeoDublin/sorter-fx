package app;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelEtichette {
    private final StringProperty titleA;
    private final StringProperty titleB;
    private final StringProperty titleC;
    private final StringProperty titleD;
    private final StringProperty A;
    private final StringProperty B;
    private final StringProperty C;
    private final StringProperty D;

    public modelEtichette(String A, String B, String C, String D) {
        this.titleA = new SimpleStringProperty("PRIMO BARCODE");
        this.titleB = new SimpleStringProperty("ULTIMO BARCODE");
        this.titleC = new SimpleStringProperty("RIFERIMENTO SCATOLO");
        this.titleD = new SimpleStringProperty("RIFERIMENTO PEDANA");
        this.A = new SimpleStringProperty(A);
        this.B = new SimpleStringProperty(B);
        this.C = new SimpleStringProperty(C);
        this.D = new SimpleStringProperty(D);
    }

    @SuppressWarnings("exports")
    public StringProperty A() { return A;}
    @SuppressWarnings("exports")
    public StringProperty B() { return B;}
    @SuppressWarnings("exports")
    public StringProperty C() { return C;}
    @SuppressWarnings("exports")
    public StringProperty D() { return D;}
    @SuppressWarnings("exports")
    public StringProperty titleA() { return titleA;}
    @SuppressWarnings("exports")
    public StringProperty titleB() { return titleB;}
    @SuppressWarnings("exports")
    public StringProperty titleC() { return titleC;}
    @SuppressWarnings("exports")
    public StringProperty titleD() { return titleD;}
}
