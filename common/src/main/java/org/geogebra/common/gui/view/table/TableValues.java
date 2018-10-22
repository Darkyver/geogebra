package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.arithmetic.Evaluatable;

/**
 * Interface for the table of values view.
 */
public interface TableValues extends View {

	/**
	 * Show the column for the Evaluatable object.
	 *
	 * @param evaluatable object to evaluate in table
	 */
	void showColumn(Evaluatable evaluatable);

	/**
	 * Hide the column for the Evaluatable object.
	 *
	 * @param evaluatable object to hide in table
	 */
	void hideColumn(Evaluatable evaluatable);

	/**
	 * Set the lower value of the x-values.
	 *
	 * @param valuesMin lower value of x-values
	 */
	void setValuesMin(float valuesMin);

	/**
	 * Get the lower value of the x-values.
	 *
	 * @return the lower value of x-values
	 */
	float getValuesMin();

	/**
	 * Set the upper value of the x-values.
	 *
	 * @param valuesMax upper value of x-values
	 */
	void setValuesMax(float valuesMax);

	/**
	 * Get the upper value of the x-values.
	 *
	 * @return the upper value of x-values
	 */
	float getValuesMax();

	/**
	 * Set the step of the x-values.
	 *
	 * @param valuesStep step of the x-values
	 */
	void setValuesStep(float valuesStep);

	/**
	 * Get the step of the x-values/
	 *
	 * @return the step of the x-values
	 */
	float getValuesStep();

	/**
	 * Get the table values model. Objects can register themselves
	 * as listeners to this model. Also table row and column information
	 * is available through this model.
	 *
	 * @return the table values model
	 */
	TableValuesModel getTableValuesModel();
}
