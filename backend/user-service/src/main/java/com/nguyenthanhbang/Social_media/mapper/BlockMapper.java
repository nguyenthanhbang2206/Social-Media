package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.BlockResponse;
import com.nguyenthanhbang.Social_media.model.Block;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlockMapper {
    BlockResponse toBlockResponse(Block block);
    List<BlockResponse> toBlockResponses(List<Block> blocks);
}

