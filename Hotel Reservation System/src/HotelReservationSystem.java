import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
public class HotelReservationSystem {
	public static void main(String[] args) {
		
			try {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb","root","roots");
				
				//Statement smt=con.createStatement();
				while(true) {
					System.out.println();
					System.out.println("Welcome to Hotel Management System");
					Scanner sc=new Scanner(System.in);
					System.out.println("Choose an option: ");
					System.out.println("1. Reserve a room");
					System.out.println("2. View Reservation");
					System.out.println("3. Get room number");
					System.out.println("4. Update Reservation info");
					System.out.println("5. Delete Reservation");
					System.out.println("0. Exit");
					int choice=sc.nextInt();
					switch(choice) {
					case 1:
						reserveRoom(con,sc);
						break;
					case 2:
						viewReservations(con);
						break;
					case 3:
						getRoomNumber(con,sc);
						break;
					case 4:
						updateReservation(con,sc);
						break;
					case 5:
						deleteReservation(con,sc);
						break;
					case 0:
						exit();
						sc.close();
						return;
					default:
						System.out.println("Invalid choise. Try again");
					}
					
				}
				
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			 
			
	}
	
	private static void reserveRoom(Connection con, Scanner sc) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
	
	static void viewReservations(Connection con) throws SQLException{
		String query="SELECT * FROM reservations;";
		
		try {
			Statement smt=con.createStatement();
			ResultSet rs=smt.executeQuery(query);
			
			System.out.println("Current Reservation");
			System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	        System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
	        System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
			
	        while(rs.next()) {
	        	int rId=rs.getInt("reservation_id");
	        	String name=rs.getNString("guest_name");
	        	int rNo=rs.getInt("room_number");
	        	String cNO=rs.getString("contact_number");
	        	String date=rs.getTimestamp("reservation_date").toString();
	        	
	        	System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        rId, name, rNo, cNO, date);
	        }
	        
	        System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
			
		}catch(SQLException e) {
			System.out.println(e);
		}
	}
	
	static void getRoomNumber(Connection con,Scanner sc) {
		try {
			System.out.println("Enter Reservation ID: ");
			int id=sc.nextInt();
			System.out.println("Enter guest name: ");
			String name=sc.next();
			
			String query="SELECT room_number FROM reservations "+
					"WHERE reservation_id = "+id+" AND guest_name= '"+
					name+"';";
			
			try(Statement smt=con.createStatement();
				ResultSet rs=smt.executeQuery(query)	){
				
				if(rs.next()) {
					int roomNo=rs.getInt("room_number");
					System.out.println("Room number for Reservation ID "+id+" and Guest "
							+name+" is: "+roomNo);
				}
				else {
					System.out.println("Reservation not found for given ID and guest name.");
				}
				
			}
			
		}catch(SQLException e) {
			System.out.println(e); 
			
		}
	}
	
	static void updateReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine(); // Consume the newline character

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation"
                    + "_id = " + reservationId;

            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	static void deleteReservation(Connection con,Scanner sc) {
		try {
			System.out.println("Enter Reservation id to delete: ");
			int reserveId=sc.nextInt();
			
			if(!reservationExists(con,reserveId)) {
				System.out.println("Reservaiton not found for given id");
				return;
			}
			String query="delete from reservations where reservation_id= "+reserveId;
			
			try(Statement smt=con.createStatement()){
				int affect=smt.executeUpdate(query);
				
				if(affect>0) {
					System.out.println("Reservation deleted successfully !");
				}
				else {
					System.out.println("Failed to delete");
				}
			}
		}catch(SQLException e) {
			System.out.println(e);
		}
	}
	
	static boolean reservationExists(Connection con,int reserveId) {
		try {
			String query="SELECT reservation_id FROM reservations WHERE reservtion_id= "+reserveId;
			
			try(Statement smt=con.createStatement();
					ResultSet rs=smt.executeQuery(query)){
				
				return rs.next();
			}
		}catch(SQLException e) {
			System.out.println(e);
			return false;
		}
		
	}
	
	static void exit() {
		System.out.println("Thank you for using this site");
		System.out.println("Exiting System");
		for(int i=0;i<5;i++) {
			System.out.print(".");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();
		System.out.println("Have a great day !!!");
	}
	
	
	
}
