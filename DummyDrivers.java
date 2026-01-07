package saksham.medrescue.saksham;

import java.util.ArrayList;
import java.util.List;

public class DummyDrivers {

    public static class Driver {
        public String name;
        public String phone;
        public String rating;

        public Driver(String name, String phone, String rating) {
            this.name = name;
            this.phone = phone;
            this.rating = rating;
        }
    }

    public static List<Driver> getDummyDrivers() {
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver("Rajiv Kumar", "+91 9876543210", "4.8"));
        drivers.add(new Driver("Amit Verma", "+91 9123456780", "4.9"));
        drivers.add(new Driver("Deepak Singh", "+91 9001122334", "4.7"));
        drivers.add(new Driver("Sandeep Sharma", "+91 8099887766", "4.5"));
        drivers.add(new Driver("Ankit Mishra", "+91 9988776655", "4.6"));
        return drivers;
    }
}
