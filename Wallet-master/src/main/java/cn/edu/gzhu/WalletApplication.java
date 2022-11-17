package cn.edu.gzhu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Li zhihao
 * @version V1.0
 * @description: TODO
 * @creat 2022-09-29-15:11
 */
@SpringBootApplication
@MapperScan("cn.edu.gzhu.mapper")
public class WalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}

}