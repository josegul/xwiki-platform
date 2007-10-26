/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
 *
 */


package com.xpn.xwiki.store;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiLock;
import com.xpn.xwiki.objects.classes.BaseClass;

import java.util.List;


public interface XWikiStoreInterface {
    public void saveXWikiDoc(XWikiDocument doc, XWikiContext context) throws XWikiException;
    public void saveXWikiDoc(XWikiDocument doc, XWikiContext context, boolean bTransaction) throws XWikiException;
    public XWikiDocument loadXWikiDoc(XWikiDocument doc, XWikiContext context) throws XWikiException;
    public void deleteXWikiDoc(XWikiDocument doc, XWikiContext context) throws XWikiException;
    public List getClassList(XWikiContext context) throws XWikiException;
    public List searchDocumentsNames(String wheresql, XWikiContext context) throws XWikiException;
    public List searchDocumentsNames(String wheresql, int nb, int start, XWikiContext context) throws XWikiException;
    public List searchDocumentsNames(String wheresql, int nb, int start, String selectColumns, XWikiContext context) throws XWikiException;

    /**
     * Search documents by passing HQL where clause values as parameters. This allows generating
     * a Named HQL query which will automatically encode the passed values (like escaping single
     * quotes). This API is recommended to be used over the other similar methods where the values
     * are passed inside the where clause and for which you'll need to do the encoding/escpaing
     * yourself before calling them.
     *
     * <p>Example</p>
     * <pre><code>
     * #set($orphans = $xwiki.searchDocuments(" where doc.fullName <> ? and (doc.parent = ? or "
     *     + "(doc.parent = ? and doc.web = ?))",
     *     ["${doc.fullName}as", ${doc.fullName}, ${doc.name}, ${doc.web}]))
     * </code></pre>
     *
     * @param parametrizedSqlClause the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>
     * @param nb the number of rows to return. If 0 then all rows are returned
     * @param start the number of rows to skip. If 0 don't skip any row
     * @param parameterValues the where clause values that replace the question marks (?)
     * @param context the XWiki context required for getting information about the execution context 
     * @return a list of document names
     * @throws XWikiException in case of error while performing the query
     */
    public List searchDocumentsNames(String parametrizedSqlClause, int nb, int start,
        List parameterValues, XWikiContext context) throws XWikiException;

    /**
     * Same as {@link #searchDocumentsNames(String, int, int, List, XWikiContext)} but returns all
     * rows.
     *
     * @see #searchDocumentsNames(String, int, int, java.util.List, com.xpn.xwiki.XWikiContext) 
     */
    public List searchDocumentsNames(String parametrizedSqlClause, List parameterValues,
        XWikiContext context) throws XWikiException;
    
    /**
     * Search documents in the storing system.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param distinctbylanguage when a document has multiple version for each language it is
     *            returned as one document a language.
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List searchDocuments(String wheresql, boolean distinctbylanguage, XWikiContext context)
        throws XWikiException;

    /**
     * Search documents in the storing system.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List searchDocuments(String wheresql, int nb, int start, XWikiContext context)
        throws XWikiException;

    /**
     * Search documents in the storing system.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param distinctbylanguage when a document has multiple version for each language it is
     *            returned as one document a language.
     * @param customMapping inject custom mapping in session.
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List searchDocuments(String wheresql, boolean distinctbylanguage, boolean customMapping,
        XWikiContext context) throws XWikiException;

    /**
     * Search documents in the storing system.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param distinctbylanguage when a document has multiple version for each language it is
     *            returned as one document a language.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List searchDocuments(String wheresql, boolean distinctbylanguage, int nb, int start,
        XWikiContext context) throws XWikiException;

    /**
     * Search documents in the storing system.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param distinctbylanguage when a document has multiple version for each language it is
     *            returned as one document a language.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param parameterValues the where clause values that replace the question marks (?).
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public List searchDocuments(String wheresql, boolean distinctbylanguage, int nb, int start,
        List parameterValues, XWikiContext context) throws XWikiException;

    /**
     * Search documents in the storing system.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param distinctbylanguage when a document has multiple version for each language it is
     *            returned as one document a language.
     * @param customMapping inject custom mapping in session.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List searchDocuments(String wheresql, boolean distinctbylanguage, boolean customMapping,
        int nb, int start, XWikiContext context) throws XWikiException;

    /**
     * Search documents in the storing system.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List searchDocuments(String wheresql, XWikiContext context) throws XWikiException;

    /**
     * Search documents in the storing system.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param distinctbylanguage when a document has multiple version for each language it is
     *            returned as one document a language.
     * @param customMapping inject custom mapping in session.
     * @param checkRight if true check for each found document if context's user has "view" rights
     *            for it.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List searchDocuments(String wheresql, boolean distinctbylanguage,
        boolean customMapping, boolean checkRight, int nb, int start, XWikiContext context)
        throws XWikiException;

    /**
     * Search documents in the storing system.
     * <p>
     * Search documents by passing HQL where clause values as parameters. This allows generating a
     * Named HQL query which will automatically encode the passed values (like escaping single
     * quotes). This API is recommended to be used over the other similar methods where the values
     * are passed inside the where clause and for which you'll need to do the encoding/escpaing
     * yourself before calling them.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param parameterValues the where clause values that replace the question marks (?).
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public List searchDocuments(String wheresql, List parameterValues, XWikiContext context)
        throws XWikiException;

    /**
     * Search documents in the storing system.
     * <p>
     * Search documents by passing HQL where clause values as parameters. This allows generating a
     * Named HQL query which will automatically encode the passed values (like escaping single
     * quotes). This API is recommended to be used over the other similar methods where the values
     * are passed inside the where clause and for which you'll need to do the encoding/escpaing
     * yourself before calling them.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param distinctbylanguage when a document has multiple version for each language it is
     *            returned as one document a language.
     * @param customMapping inject custom mapping in session.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param parameterValues the where clause values that replace the question marks (?).
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public List searchDocuments(String wheresql, boolean distinctbylanguage,
        boolean customMapping, int nb, int start, List parameterValues, XWikiContext context)
        throws XWikiException;

    /**
     * Search documents in the storing system.
     * <p>
     * Search documents by passing HQL where clause values as parameters. This allows generating a
     * Named HQL query which will automatically encode the passed values (like escaping single
     * quotes). This API is recommended to be used over the other similar methods where the values
     * are passed inside the where clause and for which you'll need to do the encoding/escpaing
     * yourself before calling them.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param parameterValues the where clause values that replace the question marks (?).
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public List searchDocuments(String wheresql, int nb, int start, List parameterValues,
        XWikiContext context) throws XWikiException;

    /**
     * Search documents in the storing system.
     * <p>
     * Search documents by passing HQL where clause values as parameters. This allows generating a
     * Named HQL query which will automatically encode the passed values (like escaping single
     * quotes). This API is recommended to be used over the other similar methods where the values
     * are passed inside the where clause and for which you'll need to do the encoding/escpaing
     * yourself before calling them.
     * 
     * @param wheresql the HQL where clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param distinctbylanguage when a document has multiple version for each language it is
     *            returned as one document a language.
     * @param customMapping inject custom mapping in session.
     * @param checkRight if true check for each found document if context's user has "view" rights
     *            for it.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param parameterValues the where clause values that replace the question marks (?).
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public List searchDocuments(String wheresql, boolean distinctbylanguage,
        boolean customMapping, boolean checkRight, int nb, int start, List parameterValues,
        XWikiContext context) throws XWikiException;

    public XWikiLock loadLock(long docId, XWikiContext context, boolean bTransaction) throws XWikiException;
    public void saveLock(XWikiLock lock, XWikiContext context, boolean bTransaction) throws XWikiException;
    public void deleteLock(XWikiLock lock, XWikiContext context, boolean bTransaction) throws XWikiException;
    public List loadLinks(long docId, XWikiContext context, boolean bTransaction) throws XWikiException;
    public List loadBacklinks(String fullName, XWikiContext context, boolean bTransaction) throws XWikiException;
    public void saveLinks(XWikiDocument doc, XWikiContext context, boolean bTransaction) throws XWikiException;
    public void deleteLinks(long docId, XWikiContext context, boolean bTransaction) throws XWikiException;
    
    /**
     * Execute a reading request with parameters and return result.
     * 
     * @param sql the HQL request clause. For example <code>" where doc.fullName
     *        <> ? and (doc.parent = ? or (doc.parent = ? and doc.web = ?))"</code>.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List search(String sql, int nb, int start, XWikiContext context) throws XWikiException;
    
    /**
     * Search documents in the storing system.
     * <p>
     * Search documents by passing HQL request values as parameters. This allows generating a
     * Named HQL query which will automatically encode the passed values (like escaping single
     * quotes). This API is recommended to be used over the other similar methods where the values
     * are passed inside the where clause and for which you'll need to do the encoding/escaping
     * yourself before calling them.
     * 
     * @param sql the HQL request.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param parameterValues the where clause values that replace the question marks (?).
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public List search(String sql, int nb, int start, List parameterValues, XWikiContext context) throws XWikiException;
    
    /**
     * Search documents in the storing system.
     * 
     * @param sql the HQL request.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param whereParams if not null add to <code>sql</code> a where clause based on a table of
     *            table containing field name, field value and compared symbol ("=", ">", etc.).
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     */
    public List search(String sql, int nb, int start, Object[][] whereParams, XWikiContext context) throws XWikiException;
    
    /**
     * Search documents in the storing system.
     * <p>
     * Search documents by passing HQL request values as parameters. This allows generating a
     * Named HQL query which will automatically encode the passed values (like escaping single
     * quotes). This API is recommended to be used over the other similar methods where the values
     * are passed inside the where clause and for which you'll need to do the encoding/escaping
     * yourself before calling them.
     * 
     * @param sql the HQL request.
     * @param nb the number of rows to return. If 0 then all rows are returned.
     * @param start the number of rows to skip. If 0 don't skip any row.
     * @param whereParams if not null add to <code>sql</code> a where clause based on a table of
     *            table containing field name, field value and compared symbol ("=", ">", etc.).
     * @param parameterValues the where clause values that replace the question marks (?).
     * @param context the XWiki context required for getting information about the execution
     *            context.
     * @return a list of XWikiDocument.
     * @throws XWikiException in case of error while performing the query.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public List search(String sql, int nb, int start, Object[][] whereParams,
        List parameterValues, XWikiContext context) throws XWikiException;
    
    public void cleanUp(XWikiContext context);
    public void createWiki(String wikiName, XWikiContext context) throws XWikiException;
    public boolean exists(XWikiDocument doc, XWikiContext context) throws XWikiException;
    public boolean isCustomMappingValid(BaseClass bclass, String custommapping1, XWikiContext context) throws XWikiException;
    public boolean injectCustomMapping(BaseClass doc1class, XWikiContext xWikiContext) throws XWikiException;
    public boolean injectCustomMappings(XWikiDocument doc, XWikiContext context) throws XWikiException;
    public List getCustomMappingPropertyList(BaseClass bclass);
    public void injectCustomMappings(XWikiContext context) throws XWikiException;
    public void injectUpdatedCustomMappings(XWikiContext context) throws XWikiException;
    public List getTranslationList(XWikiDocument doc, XWikiContext context) throws XWikiException;
}
