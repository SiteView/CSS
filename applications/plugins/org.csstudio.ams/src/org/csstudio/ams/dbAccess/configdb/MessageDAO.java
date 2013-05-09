/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.ams.dbAccess.configdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;
import org.csstudio.domain.common.strings.StringUtil;

public abstract class MessageDAO extends DAO {
	public static final int LEN_PROP = 16;
	public static final int LEN_VAL = 256;

	public static int insert(final Connection connection, final MapMessage map,
			final boolean withoutAMSProps) throws SQLException, JMSException {
		final String query1 = "INSERT INTO AMS_Message (cProperty,cValue) VALUES('%PARAM1%','%PARAM2%')";
		final String query2 = "INSERT INTO AMS_Message (iMessageID,cProperty,cValue) VALUES (?,?,?)";

		PreparedStatement preparedStatement = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String query = null;

		try {
			int messageID = Integer.MIN_VALUE;

			final Enumeration<?> enumProps = map.getMapNames();// not
																// getPropertyNames
			while (enumProps.hasMoreElements()) {
				final String propName = (String) enumProps.nextElement();

				if (withoutAMSProps) {
					if (propName.startsWith(AMS_PREFIX)) {
						continue;
					}
				}

				final String value = map.getString(propName);

				if (messageID == Integer.MIN_VALUE) {
					query = query1
							.replace(
									"%PARAM1%",
									StringUtil.checkedSubstring(propName,
											LEN_PROP))
							.replace("%PARAM2%",
									StringUtil.checkedSubstring(value, LEN_VAL));
					statement = connection.createStatement();
					statement.executeUpdate(query,
							Statement.RETURN_GENERATED_KEYS);
					resultSet = statement.getGeneratedKeys();

					if ((resultSet == null) || (resultSet.next() == false)) {
						throw new SQLException(
								"Can not determine autogenerated key");
					}

					messageID = resultSet.getInt(1);
				} else {
					if (preparedStatement == null) {
						query = query2;
						preparedStatement = connection.prepareStatement(query);
					}

					preparedStatement.setInt(1, messageID);
					preparedStatement.setString(2,
							StringUtil.checkedSubstring(propName, LEN_PROP));
					preparedStatement.setString(3,
							StringUtil.checkedSubstring(value, LEN_VAL));

					preparedStatement.execute();
				}
			}
			return messageID;
		} catch (final SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed(" + ex.getSQLState() + "): "
					+ query, ex);
			throw ex;
		} catch (final JMSException ex) {
			Log.log(Log.FATAL, ex);
			throw ex;
		} finally {
			close(statement, resultSet);
			close(preparedStatement, null);
		}
	}

	public static void select(final Connection con, final int iMessageID,
			final MapMessage msg) throws SQLException, JMSException {
		final String query = "SELECT cProperty,cValue FROM AMS_Message WHERE iMessageID = ?";
		ResultSet rs = null;
		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			st.setInt(1, iMessageID);
			rs = st.executeQuery();

			while (rs.next()) {
				msg.setString(rs.getString(1), rs.getString(2));
			}
		} catch (final SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(st, rs);
		}
	}

	public static List<MessageTObject> select(final Connection con,
			final int iMessageID) throws SQLException {
		final String query = "SELECT cProperty,cValue FROM AMS_Message WHERE iMessageID = ?";
		ResultSet rs = null;
		PreparedStatement st = null;
		final ArrayList<MessageTObject> array = new ArrayList<MessageTObject>();

		try {
			st = con.prepareStatement(query);
			st.setInt(1, iMessageID);
			rs = st.executeQuery();

			while (rs.next()) {
				array.add(new MessageTObject(iMessageID, rs.getString(1), rs
						.getString(2)));
			}
		} catch (final SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(st, rs);
		}
		return array;
	}

	public static void removeAll(final Connection con) throws SQLException {
		final String query = "DELETE FROM AMS_Message";

		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			st.executeUpdate();
		} catch (final SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(st, null);
		}
	}

	public static void remove(final Connection con, final int iMessageID)
			throws SQLException {
		final String query = "DELETE FROM AMS_Message WHERE iMessageID = ?";

		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			st.setInt(1, iMessageID);
			st.executeUpdate();
		} catch (final SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(st, null);
		}
	}

	public static void copyMessages(Connection masterDB, Connection targetDB)
			throws SQLException {
		final String query = "SELECT iMessageID, cProperty, cValue FROM AMS_Message";

		ResultSet resultSet = null;
		PreparedStatement statement = null;
		PreparedStatementHolder psth = null;

		try {
			psth = new PreparedStatementHolder();
			statement = masterDB.prepareStatement(query);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				MessageTObject messageObj = new MessageTObject(
						resultSet.getInt(1), resultSet.getString(2),
						resultSet.getString(3));
				preparedInsertFilter(targetDB, psth, messageObj);
			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(statement, resultSet);

			try {
				if (psth.pst != null) {
					psth.pst.close();
				}
			} catch (SQLException ex) {
				Log.log(Log.WARN, ex);
			}
		}
	}

	public static void insertWithMessageId(final Connection connection,
			final MapMessage map, int messageID) throws SQLException,
			JMSException {
		final String query = "INSERT INTO AMS_Message (iMessageID,cProperty,cValue) VALUES (?,?,?)";

		PreparedStatement preparedStatement = null;

		try {
			final Enumeration<?> enumProps = map.getMapNames();// not
																// getPropertyNames

			while (enumProps.hasMoreElements()) {
				final String propName = (String) enumProps.nextElement();

				if (!propName.startsWith(AMS_PREFIX)) {
					final String value = map.getString(propName);
					if (preparedStatement == null) {
						preparedStatement = connection.prepareStatement(query);
					}

					preparedStatement.setInt(1, messageID);
					preparedStatement.setString(2,
							StringUtil.checkedSubstring(propName, LEN_PROP));
					preparedStatement.setString(3,
							StringUtil.checkedSubstring(value, LEN_VAL));

					preparedStatement.execute();
				}
			}
		} catch (final SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed(" + ex.getSQLState() + "): "
					+ query, ex);
			throw ex;
		} catch (final JMSException ex) {
			Log.log(Log.FATAL, ex);
			throw ex;
		} finally {
			close(preparedStatement, null);
		}
	}

	private static void preparedInsertFilter(Connection targetDB,
			PreparedStatementHolder psth, MessageTObject messageObject)
			throws SQLException {
		final String query = "INSERT INTO AMS_Message"
				+ " (iMessageID, cProperty, cValue) VALUES(?,?,?)";

		try {
			if (psth.bMode == PreparedStatementHolder.MODE_INIT) {
				psth.pst = targetDB.prepareStatement(query);
				psth.bMode = PreparedStatementHolder.MODE_EXEC;
			}

			psth.pst.setInt(1, messageObject.getMessageID());
			psth.pst.setString(2, messageObject.getProperty());
			psth.pst.setString(3, messageObject.getValue());

			psth.pst.executeUpdate();
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
	}
}