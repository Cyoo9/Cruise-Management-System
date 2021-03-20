# cs166projectphase3

# Assumptions/Documentation:
In many cases, errors are handled smoothly by preventing the user from continuing until a valid input is made.
Other cases use specific error messages, and if not specified, the SQL error itself is caught and thrown.

These are the assumptions made:
- Assume number of cruises sold must be greater than 0
- Assume when booking a cruise, status 'W' is given when the number of seats sold is greater than the ship's capacity. Otherwise, it is 'R'
- Assume that customers preexist before adding any.

Documentation on specific features:
- Error handling for day, month, year.
- A helper function parseDate was created which validates and takes in date input.
- Features of parseDate include checking for the number of days per month, accounting for leap years, and allowing the full English name of the month to be used.
- Error handling for cruise cost, cruise sold, age, number of seats. This mostly checks if the numbers are valid, positive integers.
- Error handling for correct status, preventing input other than 'W', 'C', or 'R'


# Caleb 
- Implemented all the add functionalities besides booking cruise. 
- Implemented finding passengers count with a specific status grabbing user input.
- Implemented error handling for status types, date format (commented out to use helper function created by Charles), and integer boundaries for cruise costs, cruise sold, and number of seats.
# Charles 
- Implemented parseDate helper function to smoothly validate date input
- Implemented BookCruise, ListNumberOfAvailableSeats, ListTotalNumberofRepairsPerShip
- Implemented error handling for verifying if a cruise or customer exists
- Debugging/Editing
