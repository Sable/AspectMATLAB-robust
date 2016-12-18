function [] = target()
  global AM_GLOBAL;
  if isempty(AM_GLOBAL)
    AM_GLOBAL.dW = dW;
    AM_EntryPoint_0 = 1;
  else 
    AM_EntryPoint_0 = 0;
  end
  x1 = (1 : 10);
  AM_GLOBAL.dW.dW_a1('x1');
  AM_CVar_0 = x1;
  AM_GLOBAL.dW.dW_a1('x1');
  AM_CVar_1 = x1;
  AM_CVar_2 = sin(AM_CVar_1);
  AM_GLOBAL.dW.dW_a2();
  AM_tmpBE_0 = (AM_CVar_0 + AM_CVar_2)
  x1 = AM_tmpBE_0;
  if AM_EntryPoint_0
    AM_GLOBAL = [];
  end
end
