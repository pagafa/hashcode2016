import java.util.ArrayList;
import java.util.Collections;

public class Delivery extends Located {
	private final int order;
	private final int item;

	private Warehouse closestWarehouses;
	private int distanceClosestWarehouses;

	public Delivery(final int order, final int row, final int colum, final int item) {
		super(row, colum);
		this.order = order;
		this.item = item;
	}

	public int getItem() {
		return item;
	}

	public void setWarehouses(final Warehouse[] all) {
		ArrayList<Warehouse> warehouses = new ArrayList<>();
		for (Warehouse warehouse : all) {
			if (warehouse.hasItem(item)) {
				warehouses.add(warehouse);
			}
		}
		Collections.sort(warehouses, (o1, o2) -> o1.distance(Delivery.this) - o2.distance(Delivery.this));
		closestWarehouses = warehouses.get(0);
		distanceClosestWarehouses = distance(closestWarehouses);
	}

	public Warehouse getClosestWarehouse() {
		return closestWarehouses;
	}

	public int getWarehouseDistance() {
		return distanceClosestWarehouses;
	}

	public int getOrder() {
		return order;
	}

}
