package saksham.medrescue.saksham;

public class RideRequest {
    public String requestId;
    public String customerId;
    public double pickupLat;
    public double pickupLng;
    public double destLat;
    public double destLng;
    public String destinationName;
    public String status; // "searching", "driver_assigned", "completed", "cancelled"
    public String acceptedDriver; // driverId or null
    public long timestamp;

    public RideRequest() {} // required for Firebase

    public RideRequest(String requestId, String customerId,
                       double pickupLat, double pickupLng,
                       double destLat, double destLng,
                       String destinationName, String status, long timestamp) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.destLat = destLat;
        this.destLng = destLng;
        this.destinationName = destinationName;
        this.status = status;
        this.acceptedDriver = null;
        this.timestamp = timestamp;
    }
}
