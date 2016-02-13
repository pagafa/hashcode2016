
public class Located {
	private int row;
	private int colum;

	public Located(final int row, final int colum) {
		this.row = row;
		this.colum = colum;
	}

	public int getRow() {
		return row;
	}

	public void setRow(final int row) {
		this.row = row;
	}

	public int getColum() {
		return colum;
	}

	public void setColum(final int colum) {
		this.colum = colum;
	}

	public int distance(final Located other) {
		return (int) Math.ceil(Math.sqrt(Math.pow(row - other.getRow(), 2) + Math.pow(colum - other.getColum(), 2)));

	}
}
