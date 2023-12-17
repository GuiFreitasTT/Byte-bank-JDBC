package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;
import com.mysql.cj.xdevapi.Client;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private Connection con;

    ContaDAO(Connection connection) {
        this.con = connection;
    }

    public void save(DadosAberturaConta dadosDaConta) {
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente, true);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email, isActive)" +
                "VALUES (?,?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());
            preparedStatement.setBoolean(6, true);

            preparedStatement.execute();
            preparedStatement.close();
            con.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listAll() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM conta where isActive = true";

        try {
            preparedStatement = con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
               Integer num = resultSet.getInt(1);
               BigDecimal saldo = resultSet.getBigDecimal(2);
               String nome = resultSet.getString(3);
               String cpf = resultSet.getString(4);
               String email = resultSet.getString(5);
               Boolean estaAtiva = resultSet.getBoolean(6);

               DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
               Cliente cliente = new Cliente(dadosCadastroCliente);
               contas.add(new Conta(num, saldo, cliente, estaAtiva));
            }

            resultSet.close();
            preparedStatement.close();
            con.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return contas;

    }

    public Conta listByNumber(Integer numero) {
        String sql = "SELECT * FROM conta WHERE numero = ? and isActive = true";

        PreparedStatement ps;
        ResultSet resultSet;
        Conta conta = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, numero);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Integer numeroRecuperado = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);
                Boolean estaAtiva = resultSet.getBoolean(6);

                DadosCadastroCliente dadosCadastroCliente =
                        new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                conta = new Conta(numeroRecuperado, saldo, cliente, estaAtiva);
            }
            resultSet.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void updateValue(Integer number, BigDecimal value){
        PreparedStatement ps;
        String sql="UPDATE conta set saldo =? WHERE numero = ?";

        try{

            ps = con.prepareStatement(sql);

            ps.setBigDecimal(1, value);
            ps.setInt(2, number);

            ps.execute();
            ps.close();
            con.close();

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void delete(Integer numberOfAccount){
        String sql = "DELETE FROM conta WHERE numero = ?";

        try{
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, numberOfAccount);

            ps.execute();
            ps.close();
            con.close();

        }catch (SQLException e){
        throw new RuntimeException(e);
        }
    }

    public void alterLogical(Integer numberOfAccount){
        PreparedStatement ps;
        String sql="UPDATE conta set isActive = false WHERE numero = ?";

        try{

            ps = con.prepareStatement(sql);

            ps.setInt(1, numberOfAccount);

            ps.execute();
            ps.close();
            con.close();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}