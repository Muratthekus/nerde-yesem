package com.thekusch.nerdeyesem.viewmodel

import com.thekusch.nerdeyesem.data.Status

data class NetworkDataClass<T>(var status:Status,
                               var data:T?,
                               var msg:String?)