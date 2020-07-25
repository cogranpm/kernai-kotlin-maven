package com.parinherm

import org.eclipse.draw2d.*
import org.eclipse.draw2d.geometry.Point
import org.eclipse.draw2d.geometry.PrecisionDimension
import org.eclipse.draw2d.geometry.PrecisionPoint
import org.eclipse.draw2d.geometry.Rectangle
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Listener


class ChristmasTreeView (parent: Composite) : Composite (parent, SWT.NONE){

    lateinit var  root : ScalableFreeformLayeredPane
    lateinit var  primary : FreeformLayer
    lateinit var connections: ConnectionLayer
    val  TRUNK_WIDTH: Double = 40.0
    val TRUNK_HEIGHT: Double = 700.0
    val BRANCH_HEIGHT = 20.0
    val  trunkFigure = RectangleFigure()
    val branch =  RectangleFigure()

    init {
       // parent.layout = GridLayout (GridData.FILL_BOTH, false)
     //   layout = FillLayout(SWT.VERTICAL)
       // val lblTest: Label = Label(this, SWT.NONE)
        //lblTest.text = "I'm here"

        layout = GridLayout(GridData.FILL_BOTH, false)
        val canvas: FigureCanvas = createDiagram(this)
        canvas.layoutData = GridData(GridData.FILL_BOTH)
    }


    private fun createDiagram(parent: Composite) : FigureCanvas{
        root = ScalableFreeformLayeredPane()
        root.font = parent.font

        primary = FreeformLayer()
        primary.setLayoutManager(FreeformLayout())



        connections = ConnectionLayer()
        connections.setConnectionRouter(ShortestPathConnectionRouter(primary))
        root.add(connections, "Connections")

        trunkFigure.setPreferredSize(PrecisionDimension(TRUNK_WIDTH, TRUNK_HEIGHT))
        trunkFigure.setBackgroundColor(ColorConstants.darkGreen)
        primary.add(trunkFigure, Rectangle(PrecisionPoint(getTrunkLeft(), 0.0), trunkFigure.getPreferredSize()))


        val canvas: FigureCanvas = FigureCanvas(parent, SWT.DOUBLE_BUFFERED, LightweightSystem())
        canvas.setBackground(ColorConstants.white)
        canvas.setViewport(FreeformViewport())

        root.add(primary, "Primary")
        canvas.setContents(root)


        return canvas
    }


    fun  getTrunkLeft() : Double {
        val viewArea: Rectangle = root.clientArea
        return (viewArea.preciseWidth() / 2.0) - (TRUNK_WIDTH / 2.0)
    }

}