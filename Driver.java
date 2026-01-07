package saksham.medrescue.saksham;
//firebase auth:export users.json --project ambulance-11ff8
public class Driver {
    private String driverId;
    private String name;
    private String phone;
    private String ambulanceType;
    private double rating;
    private double latitude;
    private double longitude;

    // ✅ Empty constructor required for Firebase
    public Driver() {
    }

    // ✅ Full constructor (optional, helpful if you manually create objects)
    public Driver(String driverId, String name, String phone, String ambulanceType, double rating, double latitude, double longitude) {
        this.driverId = driverId;
        this.name = name;
        this.phone = phone;
        this.ambulanceType = ambulanceType;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // ✅ Getters and Setters
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAmbulanceType() {
        return ambulanceType;
    }

    public void setAmbulanceType(String ambulanceType) {
        this.ambulanceType = ambulanceType;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
