package com.parinherm.form

import com.parinherm.image.ImageUtils
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.events.KeyAdapter
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.ToolBar
import org.eclipse.swt.widgets.ToolItem


fun makeRecordingToolbar(parent: Composite) : Map<String, ToolItem> {

    val recordingToolbar = ToolBar(parent, SWT.NONE)
    val record = ToolItem(recordingToolbar, SWT.NONE)
    record.image = ImageUtils.getImage("media-record")

    val play = ToolItem(recordingToolbar, SWT.NONE)
    play.image =ImageUtils.getImage("media-playback-start")

    val stop = ToolItem(recordingToolbar, SWT.NONE)
    stop.image = ImageUtils.getImage("media-playback-stop")

    GridDataFactory.fillDefaults().grab(false, false).applyTo(recordingToolbar)
    return mapOf("record" to record, "play" to play, "stop" to stop)

    /*
    val recordButton = Button(parent, SWT.PUSH)
    val stopButton = Button(parent, SWT.PUSH)
    val playButton = Button(parent, SWT.PUSH)
    with(recordButton){
        image = ImageUtils.getImage("media-record")
        text = "&Record"
        toolTipText = "record"


    }
    with(playButton){
        image = ImageUtils.getImage("media-playback-start")
        text = "&Play"
        toolTipText = "play"
    }
    with(stopButton){
        image = ImageUtils.getImage("media-playback-stop")
        text = "&Stop"
        toolTipText = "stop"
    }
    GridDataFactory.fillDefaults().grab(false, false).applyTo(recordButton)
    GridDataFactory.fillDefaults().grab(false, false).applyTo(stopButton)
    GridDataFactory.fillDefaults().grab(false, false).applyTo(playButton)
    return mapOf("record" to recordButton, "play" to playButton, "stop" to stopButton)
     */
}
