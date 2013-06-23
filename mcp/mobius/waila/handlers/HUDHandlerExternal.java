package mcp.mobius.waila.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.network.Packet0x01TERequest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;
import cpw.mods.fml.common.network.PacketDispatcher;

public class HUDHandlerExternal implements IHighlightHandler {
	
	private static ArrayList<String> errs = new ArrayList<String>(); 
	
	private  List<String> handleErr(Throwable e, String className, List<String> currenttip){
		if (!errs.contains(className)){
			errs.add(className);
			mod_Waila.log.log(Level.WARNING, String.format("Catched unhandled exception : [%s] %s",className,e));
		}
		if (currenttip != null)
			currenttip.add("<ERROR>");
		
		return currenttip;
	}
	
	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		DataAccessor accessor = DataAccessor.instance;
		accessor.set(world, player, mop);
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		if (IWailaBlock.class.isInstance(block)){
			try{
				return ((IWailaBlock)block).getWailaStack(accessor, ConfigHandler.instance());
			}catch (Throwable e){
				handleErr(e, block.getClass().toString(), null);
			}
		}

		if(ExternalModulesHandler.instance().hasStackProviders(blockID)){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getStackProviders(blockID)){
				try{
					ItemStack retval = dataProvider.getWailaStack(accessor, ConfigHandler.instance());
					if (retval != null)
						return retval;					
				}catch (Throwable e){
					handleErr(e, dataProvider.getClass().toString(), null);					
				}				
			}
		}
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {
		DataAccessor accessor = DataAccessor.instance;
		accessor.set(world, player, mop);
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		if (accessor.getTileEntity() != null && mod_Waila.instance.serverPresent && (System.currentTimeMillis() - accessor.timeLastUpdate >= 250) ){
			accessor.timeLastUpdate = System.currentTimeMillis();
			PacketDispatcher.sendPacketToServer(Packet0x01TERequest.create(world, mop));
		}
		
		if (IWailaBlock.class.isInstance(block)){
			TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
			if (layout == Layout.HEADER)
				try{				
					return ((IWailaBlock)block).getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					return handleErr(e, block.getClass().toString(), currenttip);
				}					
			else if (layout == Layout.BODY)
				try{					
					return ((IWailaBlock)block).getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					return handleErr(e, block.getClass().toString(), currenttip);
				}						
		}
		
		if (layout == Layout.HEADER && ExternalModulesHandler.instance().hasHeadProviders(blockID)){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getHeadProviders(blockID))
				try{
					currenttip = dataProvider.getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = handleErr(e, dataProvider.getClass().toString(), currenttip);
				}
		}

		if (layout == Layout.BODY && ExternalModulesHandler.instance().hasBodyProviders(blockID)){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getBodyProviders(blockID))
				try{				
					currenttip = dataProvider.getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = handleErr(e, dataProvider.getClass().toString(), currenttip);
				}			
		}
		
		if (layout == Layout.HEADER && ExternalModulesHandler.instance().hasHeadProviders(block)){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getHeadProviders(block))
				try{				
					currenttip = dataProvider.getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = handleErr(e, dataProvider.getClass().toString(), currenttip);
				}			
		}

		if (layout == Layout.BODY && ExternalModulesHandler.instance().hasBodyProviders(block)){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getBodyProviders(block))
				try{				
					currenttip = dataProvider.getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = handleErr(e, dataProvider.getClass().toString(), currenttip);
				}			
		}		

		if (layout == Layout.HEADER && ExternalModulesHandler.instance().hasHeadProviders(accessor.getTileEntity())){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getHeadProviders(accessor.getTileEntity()))
				try{				
					currenttip = dataProvider.getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = handleErr(e, dataProvider.getClass().toString(), currenttip);
				}			
		}

		if (layout == Layout.BODY && ExternalModulesHandler.instance().hasBodyProviders(accessor.getTileEntity())){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getBodyProviders(accessor.getTileEntity()))
				try{
					currenttip = dataProvider.getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = handleErr(e, dataProvider.getClass().toString(), currenttip);
				}			
		}		
		
		return currenttip;
	}

}