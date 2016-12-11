package appstud.neykov.com.appstudassigment.model;

public enum PlaceType {
    BAR("bar");

    PlaceType(String typeString) {
        this.typeString = typeString;
    }

    /*package*/ String typeString() {
        return typeString;
    }

    private String typeString;
}
