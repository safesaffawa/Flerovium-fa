package com.safe.flerovium.mixin.Entity;

import com.safe.flerovium.Flerovium;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.EntityVertex;
import net.caffeinemc.mods.sodium.client.render.immediate.model.EntityRenderer;
import net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.caffeinemc.mods.sodium.client.util.Int2;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {EntityRenderer.class},
   remap = false,
   priority = 2005
)
public abstract class EntityRendererMixin {
   @Shadow
   @Final
   private static int[] CUBE_FACE_NORMAL;
   @Shadow
   @Final
   private static long[] CUBE_VERTEX_XY;
   @Shadow
   @Final
   private static long[] CUBE_VERTEX_ZW;

   public EntityRendererMixin() {
   }

   @Shadow
   private static void setVertex(int vertexIndex, float x, float y, float z, int color) {
   }

   @Shadow
   private static long writeVertex(long ptr, int vertexIndex, long packedUv, long packedOverlayLight, int packedNormal) {
      return 0L;
   }

   @Shadow
   private static void prepareNormalsIfChanged(PoseStack.Pose matrices) {
   }

   @Inject(
      method = {"renderCuboid"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onRenderCuboid(PoseStack.Pose matrices, VertexBufferWriter writer, ModelCuboid cuboid, int light, int overlay, int color, CallbackInfo ci) {
      ci.cancel();
      int cullingMask = flerovium$prepareVertices(matrices, cuboid, color);
      prepareNormalsIfChanged(matrices);
      MemoryStack stack = MemoryStack.stackPush();

      try {
         long packedOverlayLight = Int2.pack(overlay, light);
         long vertexBuffer = stack.nmalloc(64, 864);
         int vertexCount = flerovium$emitQuads(vertexBuffer, cuboid, packedOverlayLight, cullingMask);
         if (vertexCount > 0) {
            writer.push(stack, vertexBuffer, vertexCount, EntityVertex.FORMAT);
         }
      } catch (Throwable var15) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }
         }

         throw var15;
      }

      if (stack != null) {
         stack.close();
      }

   }

   @Unique
   private static int flerovium$prepareVertices(PoseStack.Pose matrices, ModelCuboid cuboid, int color) {
      Matrix4f pose = matrices.pose();
      float vxx = pose.m00() * cuboid.sizeX;
      float vxy = pose.m01() * cuboid.sizeX;
      float vxz = pose.m02() * cuboid.sizeX;
      float vyx = pose.m10() * cuboid.sizeY;
      float vyy = pose.m11() * cuboid.sizeY;
      float vyz = pose.m12() * cuboid.sizeY;
      float vzx = pose.m20() * cuboid.sizeZ;
      float vzy = pose.m21() * cuboid.sizeZ;
      float vzz = pose.m22() * cuboid.sizeZ;
      float c000x = MatrixHelper.transformPositionX(pose, cuboid.originX, cuboid.originY, cuboid.originZ);
      float c000y = MatrixHelper.transformPositionY(pose, cuboid.originX, cuboid.originY, cuboid.originZ);
      float c000z = MatrixHelper.transformPositionZ(pose, cuboid.originX, cuboid.originY, cuboid.originZ);
      setVertex(0, c000x, c000y, c000z, color);
      float c100x = c000x + vxx;
      float c100y = c000y + vxy;
      float c100z = c000z + vxz;
      setVertex(1, c100x, c100y, c100z, color);
      float c110x = c100x + vyx;
      float c110y = c100y + vyy;
      float c110z = c100z + vyz;
      setVertex(2, c110x, c110y, c110z, color);
      float c010x = c000x + vyx;
      float c010y = c000y + vyy;
      float c010z = c000z + vyz;
      setVertex(3, c010x, c010y, c010z, color);
      float c001x = c000x + vzx;
      float c001y = c000y + vzy;
      float c001z = c000z + vzz;
      setVertex(4, c001x, c001y, c001z, color);
      float c101x = c100x + vzx;
      float c101y = c100y + vzy;
      float c101z = c100z + vzz;
      setVertex(5, c101x, c101y, c101z, color);
      float c111x = c110x + vzx;
      float c111y = c110y + vzy;
      float c111z = c110z + vzz;
      setVertex(6, c111x, c111y, c111z, color);
      float c011x = c010x + vzx;
      float c011y = c010y + vzy;
      float c011z = c010z + vzz;
      setVertex(7, c011x, c011y, c011z, color);
      int cullingMask = ((ModelCuboidAccessor)cuboid).getCullMask();
      if (Flerovium.config.entityBackFaceCulling) {
         Matrix3f normal = matrices.normal();
         // FACE_NEG_X = 2 (EAST), FACE_POS_X = 4 (WEST) in Sodium 0.9.1
         // FACE_NEG_Z = 3 (NORTH), FACE_POS_Z = 5 (SOUTH)

         // X-axis: determine which face points away from camera
         float posX = c000x + c011x;
         float posY = c000y + c011y;
         float posZ = c000z + c011z;
         if (posX * normal.m00 + posY * normal.m01 + posZ * normal.m02 < 0.0F) {
            cullingMask &= ~(1 << 4); // cull WEST (FACE_POS_X), back-face when entity is left of camera
         }

         posX = c100x + c111x;
         posY = c100y + c111y;
         posZ = c100z + c111z;
         if (posX * normal.m00 + posY * normal.m01 + posZ * normal.m02 > 0.0F) {
            cullingMask &= ~(1 << 2); // cull EAST (FACE_NEG_X), back-face when entity is right of camera
         }

         // Z-axis: determine which face points away from camera
         posX = c000x + c110x;
         posY = c000y + c110y;
         posZ = c000z + c110z;
         if (posX * normal.m20 + posY * normal.m21 + posZ * normal.m22 < 0.0F) {
            cullingMask &= ~(1 << 3); // cull NORTH (FACE_NEG_Z)
         }

         posX = c001x + c111x;
         posY = c001y + c111y;
         posZ = c001z + c111z;
         if (posX * normal.m20 + posY * normal.m21 + posZ * normal.m22 > 0.0F) {
            cullingMask &= ~(1 << 5); // cull SOUTH (FACE_POS_Z)
         }

         // Y-axis: determine which face points away from camera
         posX = c000x + c101x;
         posY = c000y + c101y;
         posZ = c000z + c101z;
         if (posX * normal.m10 + posY * normal.m11 + posZ * normal.m12 < 0.0F) {
            cullingMask &= ~(1 << 0); // cull DOWN (FACE_NEG_Y)
         }

         posX = c010x + c111x;
         posY = c010y + c111y;
         posZ = c010z + c111z;
         if (posX * normal.m10 + posY * normal.m11 + posZ * normal.m12 > 0.0F) {
            cullingMask &= ~(1 << 1); // cull UP (FACE_POS_Y)
         }
      }

      return cullingMask;
   }

   @Unique
   private static int flerovium$emitQuads(long buffer, ModelCuboid cuboid, long packedOverlayLight, int cullMask) {
      long ptr = buffer;
      int[] normals = cuboid.normals;
      int[] positions = cuboid.positions;
      long[] textures = cuboid.textures;
      int vertexCount = 0;

      for(int faceIndex = 0; faceIndex < 6; ++faceIndex) {
         if ((cullMask & 1 << faceIndex) != 0) {
            int elementOffset = faceIndex * 4;
            int packedNormal = CUBE_FACE_NORMAL[normals[faceIndex]];
            ptr = writeVertex(ptr, positions[elementOffset + 0], textures[elementOffset + 0], packedOverlayLight, packedNormal);
            ptr = writeVertex(ptr, positions[elementOffset + 1], textures[elementOffset + 1], packedOverlayLight, packedNormal);
            ptr = writeVertex(ptr, positions[elementOffset + 2], textures[elementOffset + 2], packedOverlayLight, packedNormal);
            ptr = writeVertex(ptr, positions[elementOffset + 3], textures[elementOffset + 3], packedOverlayLight, packedNormal);
            vertexCount += 4;
         }
      }

      return vertexCount;
   }
}
