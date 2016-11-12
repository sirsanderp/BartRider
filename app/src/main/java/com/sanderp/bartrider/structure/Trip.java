package com.sanderp.bartrider.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander Peerna on 3/13/2016.
 */
public class Trip {
    // Trip attributes
    private String origin;
    private String origTimeMin;
    private String destination;
    private String destTimeMin;
    private int tripTime;
    private double fare;
    private double clipper;
    private double co2;
    private List<TripLeg> tripLegs;

    public Trip() {
        tripLegs = new ArrayList<>();
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setOrigTimeMin(String origTimeMin) {
        this.origTimeMin = origTimeMin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDestTimeMin(String destTimeMin) {
        this.destTimeMin = destTimeMin;
    }

    public void setTripTime(int tripTime) {
        this.tripTime = tripTime;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public void setClipper(double clipper) {
        this.clipper = clipper;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public String getOrigin() {
        return origin;
    }

    public String getOrigTimeMin() {
        return origTimeMin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDestTimeMin() {
        return destTimeMin;
    }

    public int getTripTime() {
        return tripTime;
    }

    public double getFare() {
        return fare;
    }

    public double getClipper() {
        return clipper;
    }

    public double getCo2() {
        return co2;
    }

    public void addLeg(TripLeg leg) {
        tripLegs.add(leg);
    }

    public List<TripLeg> getTripLegs() {
        return tripLegs;
    }


    public static class TripLeg {
        // Trip leg attributes
        private String transferCode;
        private String line;
        private boolean bikeFlag;
        private String trainHeadStation;
        private int load;
        private String trainId;
        private int trainIdx;

        public TripLeg() {}

        public void setTransferCode(String transferCode) {
            this.transferCode = transferCode;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public void setBikeFlag(boolean bikeFlag) {
            this.bikeFlag = bikeFlag;
        }

        public void setTrainHeadStation(String trainHeadStation) {
            this.trainHeadStation = trainHeadStation;
        }

        public void setLoad(int load) {
            this.load = load;
        }

        public void setTrainId(String trainId) {
            this.trainId = trainId;
        }

        public void setTrainIdx(int trainIdx) {
            this.trainIdx = trainIdx;
        }

        public String getTransferCode() {
            return transferCode;
        }

        public String getLine() {
            return line;
        }

        public boolean isBikeFlag() {
            return bikeFlag;
        }

        public String getTrainHeadStation() {
            return trainHeadStation;
        }

        public int getLoad() {
            return load;
        }

        public String getTrainId() {
            return trainId;
        }

        public int getTrainIdx() {
            return trainIdx;
        }
    }
}
