package com.sanderp.bartrider.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander Peerna on 3/13/2016.
 */
public class Trip implements Serializable {
    // Matches attributes in the <trip> tag.
    private String origin;
    private String destination;
    private double fare;
    private String origTimeMin;
    private String origTimeDate;
    private String destTimeMin;
    private String destTimeDate;
    private double clipper;
    private int tripTime;
    private double co2;
    private List<TripLeg> tripLegs;

    // Added from real-time estimates.
    private String estOrigDeparture;
    private String estDestArrival;

    public Trip() {
        tripLegs = new ArrayList<>();
    }

    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getOrigTimeMin() {
        return origTimeMin;
    }

    public void setOrigTimeMin(String origTimeMin) {
        this.origTimeMin = origTimeMin;
    }

    public String getOrigTimeDate() {
        return origTimeDate;
    }

    public void setOrigTimeDate(String origTimeDate) {
        this.origTimeDate = origTimeDate;
    }

    public String getDestTimeMin() {
        return destTimeMin;
    }

    public void setDestTimeMin(String destTimeMin) {
        this.destTimeMin = destTimeMin;
    }

    public String getDestTimeDate() {
        return destTimeDate;
    }

    public void setDestTimeDate(String destTimeDate) {
        this.destTimeDate = destTimeDate;
    }

    public double getClipper() {
        return clipper;
    }

    public void setClipper(double clipper) {
        this.clipper = clipper;
    }

    public int getTripTime() {
        return tripTime;
    }

    public void setTripTime(int tripTime) {
        this.tripTime = tripTime;
    }

    public double getCo2() {
        return co2;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public void addLeg(TripLeg leg) {
        tripLegs.add(leg);
    }

    public List<TripLeg> getTripLegs() {
        return tripLegs;
    }

    public String getEstOrigDeparture() {
        return estOrigDeparture;
    }

    public void setEstOrigDeparture(String estOrigDeparture) {
        this.estOrigDeparture = estOrigDeparture;
    }

    public String getEstDestArrival() {
        return estDestArrival;
    }

    public void setEstDestArrival(String estDestArrival) {
        this.estDestArrival = estDestArrival;
    }

    public static class TripLeg implements Serializable {
        // Matches attributes in the <leg> tag.
        private int order;
        private String transferCode;
        private String origin;
        private String destination;
        private String origTimeMin;
        private String origTimeDate;
        private String destTimeMin;
        private String destTimeDate;
        private String line;
        private boolean bikeFlag;
        private String trainHeadStation;
        private int load;
        private String trainId;
        private int trainIdx;

        // Added from real-time estimates.
        private String estLegDeparture;
        private String estLegArrival;

        public TripLeg() {}

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getTransferCode() {
            return transferCode;
        }

        public void setTransferCode(String transferCode) {
            this.transferCode = transferCode;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getOrigTimeMin() {
            return origTimeMin;
        }

        public void setOrigTimeMin(String origTimeMin) {
            this.origTimeMin = origTimeMin;
        }

        public String getOrigTimeDate() {
            return origTimeDate;
        }

        public void setOrigTimeDate(String origTimeDate) {
            this.origTimeDate = origTimeDate;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getDestTimeMin() {
            return destTimeMin;
        }

        public void setDestTimeMin(String destTimeMin) {
            this.destTimeMin = destTimeMin;
        }

        public String getDestTimeDate() {
            return destTimeDate;
        }

        public void setDestTimeDate(String destTimeDate) {
            this.destTimeDate = destTimeDate;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public boolean isBikeFlag() {
            return bikeFlag;
        }

        public void setBikeFlag(boolean bikeFlag) {
            this.bikeFlag = bikeFlag;
        }

        public String getTrainHeadStation() {
            return trainHeadStation;
        }

        public void setTrainHeadStation(String trainHeadStation) {
            this.trainHeadStation = trainHeadStation;
        }

        public int getLoad() {
            return load;
        }

        public void setLoad(int load) {
            this.load = load;
        }

        public String getTrainId() {
            return trainId;
        }

        public void setTrainId(String trainId) {
            this.trainId = trainId;
        }

        public int getTrainIdx() {
            return trainIdx;
        }

        public void setTrainIdx(int trainIdx) {
            this.trainIdx = trainIdx;
        }

        public String getEstLegDeparture() {
            return estLegDeparture;
        }

        public void setEstLegDeparture(String estLegDeparture) {
            this.estLegDeparture = estLegDeparture;
        }

        public String getEstLegArrival() {
            return estLegArrival;
        }

        public void setEstLegArrival(String estLegArrival) {
            this.estLegArrival = estLegArrival;
        }
    }
}
