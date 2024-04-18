package com.semmtech.laces.fetch.imports.relatics.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Workspace" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "workspace"
})
public class InnerIdentification {

    @XmlElement(name = "Workspace", required = true)
    protected String workspace;

    /**
     * Gets the value of the workspace property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWorkspace() {
        return workspace;
    }

    /**
     * Sets the value of the workspace property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWorkspace(String value) {
        this.workspace = value;
    }

}