package org.example.mymovies.dao;

import org.example.mymovies.model.MediaItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MySqlMediaItemDAOTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MySqlMediaItemDAO dao;

    @BeforeEach
    public void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        
        dao = new MySqlMediaItemDAO(mockConnection);
    }

    @Test
    public void testInsertUsesPreparedStatement() throws Exception {
        MediaItem item = new MediaItem("Inception", "A dream within a dream", "MOVIE", 2010);
        
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);

        dao.insert(item);
        
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture(), eq(Statement.RETURN_GENERATED_KEYS));
        
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("?"), "SQL must use placeholders (?) to prevent SQL injection");
        
        verify(mockPreparedStatement).setString(1, "Inception");
        verify(mockPreparedStatement).setString(2, "A dream within a dream");
        verify(mockPreparedStatement).setString(3, "MOVIE");
        verify(mockPreparedStatement).setInt(4, 2010);
        
        assertEquals(1L, item.getId());
    }
    
    @Test
    public void testSearchByTermUsesPreparedStatement() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        dao.searchByTerm("dream");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());

        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("?"), "SQL must use placeholders (?) to prevent SQL injection");

        verify(mockPreparedStatement).setString(1, "%dream%");
    }
}
