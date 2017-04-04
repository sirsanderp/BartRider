package com.sanderp.bartrider.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander Peerna on 3/13/2016.
 */
public class Trip implements Serializable {
    // Trip attributes
    private String origFull;
    private String destFull;
    private String origin;          // Abbreviated origin
    private String destination;     // Abbreviated destination
    private String origTimeMin;
    private String destTimeMin;
    private int tripTime;
    private double fare;
    private double clipper;
    private double co2;
    private List<TripLeg> tripLegs;

    public Trip() {
        tripLegs = new ArrayList<>();
    }

    public String getOrigFull() {
        return origFull;
    }
    
    public void setOrigFull(String origFull) {
        this.origFull = origFull;
    }

    public String getDestFull() {
        return destFull;
    }

    public void setDestFull(String destFull) {
        this.destFull = destFull;
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

    public String getOrigTimeMin() {
        return origTimeMin;
    }

    public void setOrigTimeMin(String origTimeMin) {
        this.origTimeMin = origTimeMin;
    }

    public String getDestTimeMin() {
        return destTimeMin;
    }

    public void setDestTimeMin(String destTimeMin) {
        this.destTimeMin = destTimeMin;
    }

    public int getTripTime() {
        return tripTime;
    }

    public void setTripTime(int tripTime) {
        this.tripTime = tripTime;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public double getClipper() {
        return clipper;
    }

    public void setClipper(double clipper) {
        this.clipper = clipper;
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

    public static class TripLeg implements Serializable {
        // Trip leg attributes
        private String transferCode;
        private String origin;
        private String origTimeMin;
        private String destination;
        private String destTimeMin;
        private String line;
        private boolean bikeFlag;
        private String trainHeadStation;
        private int load;
        private String trainId;
        private int trainIdx;

        public TripLeg() {}

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
    }
}
