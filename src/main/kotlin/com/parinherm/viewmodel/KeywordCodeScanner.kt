/***********************
 *  setting up the rules for kotlin scripts
 *
 */

package com.parinherm.viewmodel

import org.eclipse.jface.text.TextAttribute
import org.eclipse.jface.text.rules.RuleBasedScanner
import org.eclipse.jface.text.rules.SingleLineRule
import org.eclipse.jface.text.rules.Token
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Display

class KeywordCodeScanner : RuleBasedScanner() {

    val tokenVarDec = Token(TextAttribute(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN)))
    val tokenStringDec = Token(TextAttribute(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE)))

    init {
        setRules(
            arrayOf(
                SingleLineRule("var", " ", tokenVarDec, '\\'),
                SingleLineRule("val", " ", tokenVarDec, '\\'),
                SingleLineRule("\"", "\"", tokenStringDec, '\\')
            )
        )
    }
}