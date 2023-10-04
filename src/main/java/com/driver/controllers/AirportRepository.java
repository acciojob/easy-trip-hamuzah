package com.driver.controllers;


import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.*;

//@Repository
public class AirportRepository {
    HashMap<String, Airport> airportHashMap = new HashMap<>();
    HashMap<Integer, Flight> flightHashMap = new HashMap<>();

    HashMap<Integer,List<Integer>>ticketHashmap = new HashMap<>();
    HashMap<Integer, Passenger>passengerHashMap = new HashMap<>();



    public void addAirport(Airport airport) {
        airportHashMap.put(airport.getAirportName(),airport);
    }

    public String getLargestAirportName() {
        int cnt = 0;
        //Largest airport is in terms of terminals
        for(Airport airport: airportHashMap.values()){
            if(cnt >= airport.getNoOfTerminals()){
                cnt = airport.getNoOfTerminals();
            }
        }
        // Incase a tie return the Lexicographically smallest airportName
        List<String> list = new ArrayList<>();
        for(Airport airport: airportHashMap.values()){
            if (cnt == airport.getNoOfTerminals()){
                list.add(airport.getAirportName());
            }
        }
        Collections.sort(list);

        return list.get(0);
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double time = Double.MAX_VALUE;
        //Find the duration by finding the shortest flight that connects these 2 cities directly
        for (Flight flight: flightHashMap.values()){
            if(flight.getFromCity() == fromCity && flight.getToCity() == toCity){
                time = Math.min(time,flight.getDuration());
            }
        }
        //If there is no direct flight between 2 cities return -1.
        return time == Double.MAX_VALUE ? -1 : time;

    }


    public int getNumberOfPeopleOn(Date date, String airportName) {
        //Calculate the total number of people who have flights on that day on a particular airport
        // This includes both the people who have come for a flight and who have landed on an airport after their flight
        int ans=0;
        if(airportHashMap.containsKey(airportName)){
            City city = airportHashMap.get(airportName).getCity();
            for (Integer flightid: ticketHashmap.keySet()){
                Flight flight = flightHashMap.get(flightid);
                if(flight.getFlightDate().equals(date) && (flight.getToCity().equals(city) || flight.getFromCity().equals(city))){
                    ans += ticketHashmap.get(flightid).size();
                }
            }
        }

     return  ans;
    }


    public int calculateFlightFare(Integer flightId) {
        //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price

        int size = ticketHashmap.get(flightId).size();
        return 3000 + (size*50);
    }


    public String bookATicket(Integer flightId, Integer passengerId) {

        if(ticketHashmap.containsKey(flightId)){
            List<Integer>list = ticketHashmap.get(flightId);
            Flight flight = flightHashMap.get(flightId);
            //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
            //return a String "FAILURE"
            if (list.size() == flight.getMaxCapacity()){
                return "FAILURE";
            }
            //Also if the passenger has already booked a flight then also return "FAILURE".
            if (list.contains(passengerId)){
                return "FAILURE";
            }
            //else if you are able to book a ticket then return "SUCCESS"
            list.add(passengerId);
            ticketHashmap.put(flightId,list);
            return "SUCCESS";
        }else{
            List<Integer>list = new ArrayList<>();
            list.add(passengerId);
            ticketHashmap.put(flightId,list);
            return "SUCCESS";
        }

    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId

        if(ticketHashmap.containsKey(flightId)){
            boolean clear = false;
            List<Integer>passengerlist = ticketHashmap.get(flightId);
            if(passengerlist == null){
                return "FAILURE";
            }
            if (passengerlist.contains(passengerId)){
                passengerlist.remove(passengerId);
                clear = true;
            }
            if(clear){
                ticketHashmap.put(flightId,passengerlist);
                return "SUCCESS";
            }else {
                return "FAILURE";
            }
        }
        return "FAILURE";
    }


    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int count=0;
        for(List<Integer>list:ticketHashmap.values()){
            for(Integer i: list){
                if (i==passengerId){
                    count++;
                }
            }
        }
        return count;
    }

    public void addFlight(Flight flight) {
        //Return a "SUCCESS" message string after adding a flight.
        flightHashMap.put(flight.getFlightId(),flight);

      }

    public String getAirportNameFromFlightId(Integer flightId) {
        //We need to get the starting airportName from where the flight will be taking off (Hint think of City variable if that can be of some use)
        //return null incase the flightId is invalid or you are not able to find the airportName

        for (Flight flight: flightHashMap.values()){
            if (flight.getFlightId() == flightId){
                City city = flight.getFromCity();
                for (Airport airport: airportHashMap.values()){
                    if (airport.getCity().equals(city)){
                        return airport.getAirportName();
                    }
                }
            }
        }
       return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        //Calculate the total revenue that a flight could have
        //That is of all the passengers that have booked a flight till now and then calculate the revenue
        //Revenue will also decrease if some passenger cancels the flight

        if(ticketHashmap.containsKey(flightId)){
            int count = ticketHashmap.get(flightId).size();
            int rev=0;
            for(int i=0; i<count; i++){
               rev += 3000 + (i*50);
            }
            return rev;
        }
      return 0;
    }

    public void addPassenger(Passenger passenger) {
        //Add a passenger to the database
        //And return a "SUCCESS" message if the passenger has been added successfully.
        passengerHashMap.put(passenger.getPassengerId(),passenger);

    }
}
