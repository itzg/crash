/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.text.ui;

import org.crsh.text.Renderer;
import org.crsh.text.Style;

import java.util.ArrayList;
import java.util.List;

public class TableElement extends Element {

  /** . */
  ArrayList<RowElement> rows = new ArrayList<RowElement>();

  /** . */
  protected BorderStyle border;

  /** . */
  protected BorderStyle separator;

  /** . */
  private Overflow overflow;

  /** The column layout. */
  protected Layout columnLayout;

  /** The optional row row layout. */
  protected Layout rowLayout;

  /** Cell padding left. */
  private int cellPaddingLeft;

  /** Cell padding right. */
  private int cellPaddingRight;

  public TableElement() {
    this(Layout.flow(), Layout.flow());
  }

  public TableElement(int ... columns) {
    this(Layout.flow(), Layout.weighted(columns));
  }

  public TableElement(int[] rows, int[] columns) {
    this(Layout.weighted(rows), Layout.weighted(columns));
  }

  private TableElement(Layout rowLayout, Layout columnLayout) {
    this.rowLayout = rowLayout;
    this.columnLayout = columnLayout;
    this.border = null;
    this.separator = null;
    this.overflow = Overflow.WRAP;
    this.cellPaddingLeft = 0;
    this.cellPaddingRight = 0;
  }

  public TableElement add(RowElement row) {
    rows.add(row);
    return this;
  }

  public Layout getColumnLayout() {
    return columnLayout;
  }

  public void setColumnLayout(Layout columnLayout) {
    if (columnLayout == null) {
      throw new NullPointerException("Column layout cannot be null");
    }
    this.columnLayout = columnLayout;
  }

  public Layout getRowLayout() {
    return rowLayout;
  }

  public void setRowLayout(Layout rowLayout) {
    if (rowLayout == null) {
      throw new NullPointerException("Row layout cannot be null");
    }
    this.rowLayout = rowLayout;
  }

  public Renderer renderer() {
    return new TableRenderer(this);
  }

  public TableElement withColumnLayout(Layout columnLayout) {
    setColumnLayout(columnLayout);
    return this;
  }

  public TableElement withRowLayout(Layout rowLayout) {
    setRowLayout(rowLayout);
    return this;
  }

  public List<RowElement> getRows() {
    return rows;
  }

  public BorderStyle getBorder() {
    return border;
  }

  public void setBorder(BorderStyle border) {
    this.border = border;
  }

  public TableElement border(BorderStyle border) {
    setBorder(border);
    return this;
  }

  public BorderStyle getSeparator() {
    return separator;
  }

  public void setSeparator(BorderStyle separator) {
    this.separator = separator;
  }

  public TableElement collapse() {
    setSeparator(null);
    return this;
  }

  public TableElement separator(BorderStyle separator) {
    setSeparator(separator);
    return this;
  }

  public void setOverflow(Overflow overflow) {
    this.overflow = overflow;
  }

  public final Overflow getOverflow() {
    return overflow;
  }

  public TableElement overflow(Overflow overflow) {
    setOverflow(overflow);
    return this;
  }

  public int getCellPaddingLeft() {
    return cellPaddingLeft;
  }

  public void setCellPaddingLeft(int cellPaddingLeft) {
    if (cellPaddingLeft < 0) {
      throw new IllegalArgumentException("No negative cell padding left accepted");
    }
    this.cellPaddingLeft = cellPaddingLeft;
  }

  public TableElement cellPaddingLeft(int cellPaddingLeft) {
    setCellPaddingLeft(cellPaddingLeft);
    return this;
  }

  public int getCellPaddingRight() {
    return cellPaddingRight;
  }

  public void setCellPaddingRight(int cellPaddingRight) {
    if (cellPaddingRight < 0) {
      throw new IllegalArgumentException("No negative cell padding right accepted");
    }
    this.cellPaddingRight = cellPaddingRight;
  }

  public TableElement cellPaddingRight(int cellPaddingRight) {
    setCellPaddingRight(cellPaddingRight);
    return this;
  }

  @Override
  public TableElement style(Style.Composite style) {
    return (TableElement)super.style(style);
  }
}
