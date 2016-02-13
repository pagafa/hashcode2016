import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class App {
	public static void main(final String[] args) {
		App app = new App();
		app.load("busy_day.in");
		// app.info();
		app.start();
	}

	private int rows;
	private int colums;
	private int turns;

	private Dron[] drones;
	private Warehouse[] warehouses;
	private ArrayList<Delivery> deliveries;

	private int numberDrones;
	private int maxPayload;

	private int numberOfProductTypes;
	private int[] productTypeWeights;

	private int numberWarehouses;
	private int numberOrders;

	public void start() {
		ArrayList<String> cmds = new ArrayList<>();
		int t = 0;
		while (!deliveries.isEmpty()) {
			System.out.println("T: " + t + "\t" + deliveries.size());
			for (Dron dron : drones) {
				if (dron.getT() <= t) {
					for (Delivery delivery : deliveries) {
						delivery.setWarehouses(warehouses);
					}

					if (!deliveries.isEmpty()) {
						dron.setDeliveris(deliveries);

						Delivery delivery = dron.getFirstDelivery();
						Warehouse warehouse = delivery.getClosestWarehouse();

						deliveries.remove(delivery);
						if (!warehouse.getItem(delivery.getItem())) {
							System.out.println("No product available");
							return;
						}

						cmds.add(dron.getId() + " L " + warehouse.getId() + " " + delivery.getItem() + " " + 1);
						int time = dron.distance(warehouse) + 1;
						cmds.add(dron.getId() + " D " + delivery.getOrder() + " " + delivery.getItem() + " " + 1);
						time = time + warehouse.distance(delivery) + 1;
						dron.setRow(delivery.getRow());
						dron.setColum(delivery.getColum());
						dron.setT(t + time);
						System.out.println("D(" + dron.getId() + "): " + dron.getT());
					} else {
						cmds.add(dron.getId() + " W " + 1);
					}
				}
			}
			t++;
		}
		System.out.println("---------------------------------------------");
		System.out.println(cmds.size());
		for (String cmd : cmds) {
			System.out.println(cmd);
		}
	}

	public void info() {
		System.out.println("Drones: " + drones.length);
		System.out.println("Warehouses: " + warehouses.length);
		for (Warehouse warehouse : warehouses) {
			for (int i = 0; i < numberOfProductTypes; i++) {
				System.out.println("(" + warehouse.getId() + ") " + warehouse.getStockOfItem(i) + " ");
			}
		}
		System.out.println();
		System.out.println("Deliveris: " + deliveries.size());
		for (Delivery delivery : deliveries) {
			System.out.println(delivery.getRow() + " " + delivery.getColum() + " " + delivery.getItem());
		}
		System.out.println("Products: " + productTypeWeights.length);
	}

	public boolean load(final String file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			// 1 linea
			String[] parts = getNextLineParts(br);
			rows = getInt(parts[0]);
			colums = getInt(parts[1]);
			numberDrones = getInt(parts[2]);
			turns = getInt(parts[3]);
			maxPayload = getInt(parts[4]);

			// 2 linea
			parts = getNextLineParts(br);
			numberOfProductTypes = getInt(parts[0]);

			// 3 linea
			parts = getNextLineParts(br);
			if (parts.length != numberOfProductTypes) {
				System.out.println("Invalid size of products weigths");
				return false;
			}
			productTypeWeights = new int[numberOfProductTypes];
			for (int i = 0; i < numberOfProductTypes; i++) {
				productTypeWeights[i] = getInt(parts[i]);
			}

			// 4 linea
			parts = getNextLineParts(br);
			numberWarehouses = getInt(parts[0]);

			warehouses = new Warehouse[numberWarehouses];
			for (int i = 0; i < numberWarehouses; i++) {
				// Location
				parts = getNextLineParts(br);
				int row = getInt(parts[0]);
				int colum = getInt(parts[1]);

				Warehouse warehouse = new Warehouse(i, row, colum, numberOfProductTypes);

				// Products available
				parts = getNextLineParts(br);
				if (parts.length != numberOfProductTypes) {
					System.out.println("Invalid size of products availability for warehouse " + i);
					return false;
				}
				for (int j = 0; j < numberOfProductTypes; j++) {
					warehouse.setItemNumber(j, getInt(parts[j]));
				}
				warehouses[i] = warehouse;
			}

			// Line number ordes
			parts = getNextLineParts(br);
			numberOrders = getInt(parts[0]);

			deliveries = new ArrayList<>();
			for (int i = 0; i < numberOrders; i++) {
				// Location
				parts = getNextLineParts(br);
				int row = getInt(parts[0]);
				int colum = getInt(parts[1]);

				// Number of items
				parts = getNextLineParts(br);
				int numberOfItems = getInt(parts[0]);

				// Item list
				parts = getNextLineParts(br);
				if (parts.length != numberOfItems) {
					System.out.println("Invalid size of item list for order " + i);
					return false;
				}
				for (int j = 0; j < numberOfItems; j++) {
					int item = getInt(parts[j]);
					deliveries.add(new Delivery(i, row, colum, item));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int rowWareHouse0 = warehouses[0].getRow();
		int columWareHouse0 = warehouses[0].getColum();
		drones = new Dron[numberDrones];
		for (int i = 0; i < numberDrones; i++) {
			Dron dron = new Dron(i, maxPayload, rowWareHouse0, columWareHouse0);
			drones[i] = dron;
		}
		return true;
	}

	private String[] getNextLineParts(final BufferedReader br) throws IOException {
		String line = br.readLine();
		return line.split(" ");
	}

	private int getInt(final String value) {
		return Integer.parseInt(value);
	}
}