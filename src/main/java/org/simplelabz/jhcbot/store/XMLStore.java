package org.simplelabz.jhcbot.store;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author simple
 */
public class XMLStore extends DataStore
{
    private final String name;
    private final String chan;
    private final File datafile;
    
    public XMLStore(String name, String chan)
    {
        this.name = name;
        this.chan = chan;
        
        String datadir = "";
        if(Conf.DATADIR!=null && new File(Conf.DATADIR).exists())
            datadir = Conf.DATADIR+System.getProperty("file.separator");
        datafile = new File(datadir+DataStore.DB_NAME+".xml");
        
        Document doc = getDocument();
        // first verify and correct root element
        String rootname="jhcbot", vername="version";
        Element root = doc.getDocumentElement();
        if(root==null || !root.getTagName().equals(rootname))
        {
            if(root!=null)root.setNodeValue(rootname);
            else
            {
                root = doc.createElement(rootname);
                doc.appendChild(root);
            }
        }

        if(!root.hasAttribute(vername))
            root.setAttributeNode(doc.createAttribute(vername));
        root.setAttribute(vername, Conf.VERSION);

        // now verify and correct the store name element
        getStore(doc);
        // write out the doc
        finalize(doc);
    }

    @Override
    public void store(DataEntry entry)
    {
        Document doc = getDocument();
        Element store = getStore(doc);
        Element keyElem = doc.createElement(entry.getKey());
        keyElem.setAttribute("id", Utils.generateRandomInt(4));
        keyElem.setAttribute("chan", chan);
        if(entry.getValue()!=null)keyElem.setTextContent(entry.getValue());
        store.appendChild(keyElem);
        finalize(doc);
    }

    @Override
    public String[] getKeys()
    {
        Document doc = getDocument();
        Element store = getStore(doc);
        ArrayList<String> values = new ArrayList<>();
        for (Node child = store.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if(child instanceof Element && chan.equals(((Element)child).getAttribute("chan")))
            {
                Element data = (Element) child;
                String key = data.getTagName();
                if(!values.contains(key))
                    values.add(key);
            }
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public DataEntry[] get(String key)
    {
        Document doc = getDocument();
        Element store = getStore(doc);
        ArrayList<DataEntry> values = new ArrayList<>();
        for (Node child = store.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child instanceof Element && chan.equals(((Element)child).getAttribute("chan")) && key.equals(child.getNodeName()))
            {
                Element data = (Element) child;
                values.add(new DataEntry(Integer.parseInt(data.getAttribute("id")),key,data.getTextContent()));
            }
        }
        return values.toArray(new DataEntry[values.size()]);
    }

    @Override
    public void delete(int key)
    {
        Document doc = getDocument();
        Element store = getStore(doc);
        for (Node child = store.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child instanceof Element && Integer.parseInt(((Element) child).getAttribute("id"))==key)
            {
                store.removeChild(child);
                break;
            }
        }
        finalize(doc);
    }

    private Element getStore(Document doc)
    {
        Element store = getChild(doc.getDocumentElement(), name);
        if(store==null)
        {
            store = doc.createElement(name);
            doc.getDocumentElement().appendChild(store);
        }
        return store;
    }

    private Element getChild(Element parent, String name)
    {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child instanceof Element && name.equals(child.getNodeName()))
            {
                return (Element) child;
            }
        }
        return null;
    }

    private Document getDocument()
    {
        Document doc = null;
        try
        {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            if(datafile.exists())
            {
                try
                {
                    doc = db.parse(datafile);
                }
                catch(SAXException ex)
                {
                    doc = db.newDocument();
                }
                catch(IOException ex)
                {
                    Logger.getLogger(XMLStore.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
                doc = db.newDocument();
        }
        catch (ParserConfigurationException ex)
        {
            Logger.getLogger(XMLStore.class.getName()).log(Level.SEVERE, null, ex);
        }

        return doc;
    }

    private void finalize(Document doc)
    {
        try
        {
            Transformer tfmr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(datafile);
            //try encypted store

            tfmr.transform(source, result);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XMLStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(XMLStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
