import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class App {
	public static void main(final String[] args) {
		App app = null;

		app = new App("busy_day");
		app.load();
		app.start();

		/*
		 * app = new App("mother_of_all_warehouses"); app.load(); app.start();
		 *
		 * app = new App("redundancy"); app.load(); app.start();
		 */
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

	private final String name;

	public App(final String name) {
		this.name = name;
	}

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
					int weight = 0;

					if (!deliveries.isEmpty()) {
						dron.setDeliveris(deliveries);

						Delivery delivery = dron.getFirstDelivery();
						Warehouse warehouse = delivery.getClosestWarehouse();

						deliveries.remove(delivery);
						if (!warehouse.getItem(delivery.getItem())) {
							System.out.println("No product available");
							return;
						}
						//
						weight = productTypeWeights[delivery.getItem()];
						Delivery last = delivery;
						Delivery other = null;
						ArrayList<Delivery> others = new ArrayList<>();
						while ((other = other(warehouse, weight, last, delivery.getItem())) != null) {
							weight = weight + productTypeWeights[delivery.getItem()];
							others.add(other);

							deliveries.remove(other);
							if (!warehouse.getItem(delivery.getItem())) {
								System.out.println("No product available");
								return;
							}
						}
						//

						cmds.add(dron.getId() + " L " + warehouse.getId() + " " + delivery.getItem() + " "
								+ (1 + others.size()));
						int time = dron.distance(warehouse) + 1;
						cmds.add(dron.getId() + " D " + delivery.getOrder() + " " + delivery.getItem() + " " + 1);
						time = time + warehouse.distance(delivery) + 1;
						dron.setRow(delivery.getRow());
						dron.setColum(delivery.getColum());
						dron.setT(t + time);

						//
						last = delivery;
						for (Delivery extra : others) {
							cmds.add(dron.getId() + " D " + extra.getOrder() + " " + extra.getItem() + " " + 1);
							time = time + last.distance(extra) + 1;
							last = extra;
						}
						//

						System.out.println("D(" + dron.getId() + "): " + dron.getT());
					} else {
						cmds.add(dron.getId() + " W " + 1);
					}
				}
			}
			t++;
		}
		System.out.println("---------------------------------------------");
		try {
			FileWriter fw = new FileWriter(name + ".out");
			System.out.println(cmds.size());
			fw.write(cmds.size() + "\n");
			for (String cmd : cmds) {
				System.out.println(cmd);
				fw.write(cmd + "\n");
			}
			fw.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private Delivery other(final Warehouse warehouse, int weight, final Delivery last, final int idItem) {
		if (warehouse.hasItem(idItem)) {
			weight = weight + productTypeWeights[idItem];
			if (weight <= maxPayload) {
				ArrayList<Delivery> deliveriesForItem = new ArrayList<>();
				for (Delivery tmp : deliveries) {
					if (tmp.getItem() == idItem) {
						deliveriesForItem.add(tmp);
					}
				}

				if (!deliveriesForItem.isEmpty()) {
					Collections.sort(deliveriesForItem, (o1, o2) -> (last.distance(o1) - last.distance(o2)));
					return deliveriesForItem.get(0);
				}
			}
		}
		return null;
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

	public boolean load() {
		try (BufferedReader br = new BufferedReader(new FileReader(name + ".in"))) {
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