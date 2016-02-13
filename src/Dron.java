import java.util.ArrayList;
import java.util.Collections;

public class Dron extends Located {
	private final int id;
	private final int maxPayload;
	private int t;

	private final ArrayList<Integer> items;
	private int payload;

	private Delivery firstDelivery;

	public Dron(final int id, final int maxPayload, final int row, final int colum) {
		super(row, colum);
		this.id = id;
		this.maxPayload = maxPayload;

		t = 0;
		payload = 0;
		items = new ArrayList<>();
	}

	public boolean addItem(final int item, final int weight) {
		if ((payload + weight) > maxPayload) {
			return false;
		}
		items.add(item);
		payload = payload + weight;
		return true;
	}

	public int getPayload() {
		return payload;
	}

	public void setDeliveris(final ArrayList<Delivery> deliveries) {
		ArrayList<Delivery> list = new ArrayList<>(deliveries.size());
		list.addAll(deliveries);
		Collections.sort(list, (o1, o2) ->

		(o1.getWarehouseDistance() + o1.getClosestWarehouse().distance(Dron.this))

				-

		(o2.getWarehouseDistance() + o2.getClosestWarehouse().distance(Dron.this)));

		firstDelivery = list.get(0);
	}

	public Delivery getFirstDelivery() {
		return firstDelivery;
	}

	public void setFirstDelivery(final Delivery firstDelivery) {
		this.firstDelivery = firstDelivery;
	}

	public int getId() {
		return id;
	}

	public int getT() {
		return t;
	}

	public void setT(final int t) {
		this.t = t;
	}

}
