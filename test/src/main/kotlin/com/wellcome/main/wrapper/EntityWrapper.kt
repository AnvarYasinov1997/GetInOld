package com.wellcome.main.wrapper

import com.wellcome.main.entity.BaseEntity

data class EntityWrapper<T : BaseEntity>(val entity: T, val delegates: MutableList<Delegate> = mutableListOf())