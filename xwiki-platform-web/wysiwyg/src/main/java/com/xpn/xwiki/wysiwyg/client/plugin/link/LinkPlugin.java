/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
package com.xpn.xwiki.wysiwyg.client.plugin.link;

import java.util.HashMap;
import java.util.Map;

import org.xwiki.gwt.dom.client.Element;
import org.xwiki.gwt.dom.client.Range;

import com.xpn.xwiki.wysiwyg.client.Wysiwyg;
import com.xpn.xwiki.wysiwyg.client.plugin.image.ImageConfig;
import com.xpn.xwiki.wysiwyg.client.plugin.internal.AbstractPlugin;
import com.xpn.xwiki.wysiwyg.client.plugin.link.exec.CreateLinkExecutable;
import com.xpn.xwiki.wysiwyg.client.plugin.link.exec.LinkExecutableUtils;
import com.xpn.xwiki.wysiwyg.client.plugin.link.exec.UnlinkExecutable;
import com.xpn.xwiki.wysiwyg.client.plugin.link.ui.LinkWizard;
import com.xpn.xwiki.wysiwyg.client.util.Config;
import com.xpn.xwiki.wysiwyg.client.widget.rta.RichTextArea;
import com.xpn.xwiki.wysiwyg.client.widget.rta.cmd.Command;
import com.xpn.xwiki.wysiwyg.client.widget.rta.cmd.Executable;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.Wizard;
import com.xpn.xwiki.wysiwyg.client.widget.wizard.WizardListener;

/**
 * Rich text editor plug-in for inserting links, using a dialog to get link settings from the user. It installs two
 * buttons in the toolbar, for its two actions: link and unlink.
 * 
 * @version $Id$
 */
public class LinkPlugin extends AbstractPlugin implements WizardListener
{
    /**
     * The wizard to create a link.
     */
    private Wizard linkWizard;

    /**
     * The menu extension of this plugin.
     */
    private LinkMenuExtension menuExtension;

    /**
     * The link metadata extractor, to handle the link metadata.
     */
    private LinkMetaDataExtractor metaDataExtractor;

    /**
     * Map of the original link related executables, which will be replaced with custom executables by this plugin.
     */
    private Map<Command, Executable> originalExecutables;

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPlugin#init(Wysiwyg, RichTextArea, Config)
     */
    public void init(Wysiwyg wysiwyg, RichTextArea textArea, Config config)
    {
        super.init(wysiwyg, textArea, config);

        // add the custom executables
        Executable createLinkExec =
            getTextArea().getCommandManager().registerCommand(Command.CREATE_LINK, new CreateLinkExecutable());
        Executable unlinkExec =
            getTextArea().getCommandManager().registerCommand(Command.UNLINK, new UnlinkExecutable());
        if (createLinkExec != null || unlinkExec != null) {
            originalExecutables = new HashMap<Command, Executable>();
        }
        if (createLinkExec != null) {
            originalExecutables.put(Command.CREATE_LINK, createLinkExec);
        }
        if (unlinkExec != null) {
            originalExecutables.put(Command.UNLINK, unlinkExec);
        }

        menuExtension = new LinkMenuExtension(this);
        getUIExtensionList().add(menuExtension);

        // Initialize the metadata extractor, to handle link metadatas
        metaDataExtractor = new LinkMetaDataExtractor();
        // do the initial extracting on the loaded document
        metaDataExtractor.onInnerHTMLChange(getTextArea().getDocument().getDocumentElement());
        getTextArea().getDocument().addInnerHTMLListener(metaDataExtractor);
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPlugin#destroy()
     */
    public void destroy()
    {
        // restore previous executables
        if (originalExecutables != null) {
            for (Map.Entry<Command, Executable> entry : originalExecutables.entrySet()) {
                getTextArea().getCommandManager().registerCommand(entry.getKey(), entry.getValue());
            }
        }

        if (metaDataExtractor != null) {
            getTextArea().getDocument().removeInnerHTMLListener(metaDataExtractor);
            metaDataExtractor = null;
        }

        // destroy menu extension
        menuExtension.destroy();
        super.destroy();
    }

    /**
     * Handles the creation of a link of the specified type: prepares and shows the link wizard.
     * 
     * @param linkType the type of link to insert
     */
    public void onLinkInsert(LinkConfig.LinkType linkType)
    {
        LinkConfig linkParams = getLinkParams();
        linkParams.setType(linkType);
        dispatchLinkWizard(linkParams);
    }

    /**
     * Handles the edit of a link: prepares and shows the link wizard.
     */
    public void onLinkEdit()
    {
        LinkConfig editParams = getLinkParams();
        dispatchLinkWizard(editParams);
    }

    /**
     * Instantiates and runs the correct wizard for the passed link.
     * 
     * @param linkParams the parameters of the link to be configured through the wizard
     */
    protected void dispatchLinkWizard(LinkConfig linkParams)
    {
        switch (linkParams.getType()) {
            case WIKIPAGE:
            case NEW_WIKIPAGE:
                getLinkWizard().start("wikipage", linkParams);
                break;
            case ATTACHMENT:
                getLinkWizard().start("attachment", linkParams);
                break;
            case EMAIL:
                getLinkWizard().start("email", linkParams);
                break;
            case EXTERNAL:
            default:
                getLinkWizard().start("webpage", linkParams);
                break;
        }
    }

    /**
     * Returns the link wizard.
     * 
     * @return the link wizard.
     */
    private Wizard getLinkWizard()
    {
        if (linkWizard == null) {
            linkWizard = new LinkWizard();
            linkWizard.addWizardListener(this);
        }
        return linkWizard;
    }

    /**
     * @return the link parameters for the current position of the cursor.
     */
    protected LinkConfig getLinkParams()
    {
        String configString = getTextArea().getCommandManager().getStringValue(Command.CREATE_LINK);
        if (configString != null) {
            return getEditLinkParams(configString);
        } else {
            return getCreateLinkParams();
        }
    }

    /**
     * Prepares the link parameters for a link edition, from the passed link parameter, as returned by the
     * {@link CreateLinkExecutable#getParameter(RichTextArea)}.
     * 
     * @param linkCommandParameter the parameter of the executed {@link Command#CREATE_LINK} command.
     * @return the link parameters for link editing
     */
    protected LinkConfig getEditLinkParams(String linkCommandParameter)
    {
        LinkConfig linkParam = new LinkConfig();
        linkParam.fromJSON(linkCommandParameter);
        // set the focus on the text area otherwise IE will fail with an "invalid argument" when using the selection
        getTextArea().setFocus(true);
        Range range = getTextArea().getDocument().getSelection().getRangeAt(0);
        Element wrappingAnchor = LinkExecutableUtils.getSelectedAnchor(getTextArea());
        // check the content of the wrapping anchor, if it's an image, it should be handled specially
        if (wrappingAnchor.getChildNodes().getLength() == 1
            && wrappingAnchor.getChildNodes().getItem(0).getNodeName().equalsIgnoreCase("img")) {
            Range imageRange = getTextArea().getDocument().createRange();
            imageRange.selectNode(wrappingAnchor.getChildNodes().getItem(0));
            getTextArea().getDocument().getSelection().removeAllRanges();
            getTextArea().getDocument().getSelection().addRange(imageRange);
            String imageParam = getTextArea().getCommandManager().getStringValue(Command.INSERT_IMAGE);
            if (imageParam != null) {
                // it's an image selection, set the label readonly and put the image filename in the label text
                ImageConfig imgConfig = new ImageConfig();
                imgConfig.fromJSON(imageParam);
                linkParam.setLabelText(imgConfig.getImageFileName());
                linkParam.setReadOnlyLabel(true);
            } else {
                linkParam.setLabelText(wrappingAnchor.getInnerText());
            }
        }
        // move the selection around the link, to replace it properly upon edit
        range.selectNode(wrappingAnchor);
        getTextArea().getDocument().getSelection().removeAllRanges();
        getTextArea().getDocument().getSelection().addRange(range);

        return linkParam;
    }

    /**
     * Prepares the link parameters for a link creation, i.e. sets the link labels.
     * 
     * @return the link parameters for link creation
     */
    protected LinkConfig getCreateLinkParams()
    {
        LinkConfig config = new LinkConfig();
        // set the focus on the text area otherwise IE will fail with an "invalid argument" when using the selection
        getTextArea().setFocus(true);
        config.setLabel(getTextArea().getDocument().getSelection().getRangeAt(0).toHTML());
        // Check the special case when the selection is an image and add a link on an image
        String imageParam = getTextArea().getCommandManager().getStringValue(Command.INSERT_IMAGE);
        if (imageParam != null) {
            // it's an image selection, set the label readonly and put the image filename in the label text
            ImageConfig imgConfig = new ImageConfig();
            imgConfig.fromJSON(imageParam);
            config.setLabelText(imgConfig.getImageFileName());
            config.setReadOnlyLabel(true);
        } else {
            config.setLabelText(getTextArea().getDocument().getSelection().getRangeAt(0).toString());
            config.setReadOnlyLabel(false);
        }
        return config;
    }

    /**
     * Executed when the unlink button is clicked.
     */
    public void onUnlink()
    {
        getTextArea().getCommandManager().execute(Command.UNLINK);
    }

    /**
     * {@inheritDoc}. Handles wizard finish by creating the link HTML block from the {@link LinkConfig} setup through
     * the wizard and executing the {@link Command#CREATE_LINK} with it.
     */
    public void onFinish(Wizard sender, Object result)
    {
        // build the HTML block from the configuration data
        String linkHTML = LinkHTMLGenerator.getInstance().getLinkHTML((LinkConfig) result);
        // insert the built HTML
        getTextArea().getCommandManager().execute(Command.CREATE_LINK, linkHTML);
    }

    /**
     * {@inheritDoc}.
     */
    public void onCancel(Wizard sender)
    {
        // return the focus to the text area
        getTextArea().setFocus(true);
    }
}
