package cn.edu.gzhu.service.impl;

import cn.edu.gzhu.entity.TransactionDTO;
import cn.edu.gzhu.entity.TransactionReq;
import cn.edu.gzhu.mapper.WalletMapper;
import cn.edu.gzhu.service.WalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("WalletService")
public class WalletServiceImpl extends ServiceImpl<WalletMapper, TransactionDTO> implements WalletService {

    public IPage<TransactionDTO> selectAll(TransactionReq reqDTO){
        LambdaQueryWrapper<TransactionDTO> wrapper = new LambdaQueryWrapper<>();
        if (reqDTO.getHash() != null){
            wrapper.eq(TransactionDTO::getTxHash, reqDTO.getHash());
        }
        wrapper.orderByDesc(TransactionDTO::getId);
        IPage<TransactionDTO> iPage = new Page<>(reqDTO.getPageNumber(), reqDTO.getPageSize());
        IPage<TransactionDTO> data = this.page(iPage, wrapper);
        return data;
    }
}
