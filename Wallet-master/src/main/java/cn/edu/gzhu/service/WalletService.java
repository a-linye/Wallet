package cn.edu.gzhu.service;

import cn.edu.gzhu.entity.TransactionDTO;
import cn.edu.gzhu.entity.TransactionReq;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WalletService  extends IService<TransactionDTO> {
    IPage<TransactionDTO> selectAll(TransactionReq reqDTO);
}
