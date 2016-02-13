public class Warehouse extends Located {
	private final int id;

	private final int[] items;

	public Warehouse(final int id, final int row, final int colum, final int numberOfItemTypes) {
		super(row, colum);
		this.id = id;
		items = new int[numberOfItemTypes];
	}

	public void setItemNumber(final int id, final int amount) {
		items[id] = amount;
	}

	public boolean hasItem(final int idItem) {
		if (items[idItem] > 0) {
			return true;
		}
		return false;
	}

	public boolean getItem(final int idItem) {
		if (items[idItem] > 0) {
			items[idItem]--;
			return true;
		}
		return false;
	}

	public int getStockOfItem(final int idItem) {
		return items[idItem];
	}

	public void addItem(final int idItem) {
		items[idItem]++;
	}

	public int getId() {
		return id;
	}

}
